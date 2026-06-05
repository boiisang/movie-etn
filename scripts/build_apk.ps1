param(
    [string]$ProjectRoot = ""
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($ProjectRoot)) {
    $ProjectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
} else {
    $ProjectRoot = Resolve-Path $ProjectRoot
}

$toolsRoot = "C:\Users\joels\RealProject\.codex-tools"
$env:JAVA_HOME = Join-Path $toolsRoot "jdk17\jdk-17.0.19+10"
$env:ANDROID_HOME = Join-Path $toolsRoot "android-sdk"
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
$env:PATH = "$($env:JAVA_HOME)\bin;$($env:ANDROID_HOME)\platform-tools;$($env:ANDROID_HOME)\build-tools\34.0.0;$env:PATH"

$java = Join-Path $env:JAVA_HOME "bin\java.exe"
$javac = Join-Path $env:JAVA_HOME "bin\javac.exe"
$jar = Join-Path $env:JAVA_HOME "bin\jar.exe"
$keytool = Join-Path $env:JAVA_HOME "bin\keytool.exe"
$bt = Join-Path $env:ANDROID_HOME "build-tools\34.0.0"
$aapt2 = Join-Path $bt "aapt2.exe"
$d8 = Join-Path $bt "d8.bat"
$zipalign = Join-Path $bt "zipalign.exe"
$apksigner = Join-Path $bt "apksigner.bat"
$androidJar = Join-Path $env:ANDROID_HOME "platforms\android-34\android.jar"

foreach ($p in @($java, $javac, $jar, $keytool, $aapt2, $d8, $zipalign, $apksigner, $androidJar)) {
    if (-not (Test-Path -LiteralPath $p)) {
        throw "Missing build dependency: $p"
    }
}

$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$build = Join-Path $ProjectRoot "build\manual"
$compiled = Join-Path $build "compiled"
$classes = Join-Path $build "classes"
$dex = Join-Path $build "dex"
$gen = Join-Path $build "generated"
$logs = Join-Path $ProjectRoot "logs\build"
New-Item -ItemType Directory -Force -Path $compiled, $classes, $dex, $gen, $logs | Out-Null

Remove-Item -LiteralPath $compiled, $classes, $dex, $gen -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $compiled, $classes, $dex, $gen | Out-Null

$manifest = Join-Path $ProjectRoot "app\src\main\AndroidManifest.xml"
$res = Join-Path $ProjectRoot "app\src\main\res"
$sourceRoot = Join-Path $ProjectRoot "app\src\main\java"

$flat = Join-Path $compiled "resources.zip"
& $aapt2 compile --dir $res -o $flat
if ($LASTEXITCODE -ne 0) { throw "aapt2 compile failed" }

$unsigned = Join-Path $ProjectRoot "builds\unsigned\JoelStream-$stamp-unsigned.apk"
New-Item -ItemType Directory -Force -Path (Split-Path $unsigned) | Out-Null
& $aapt2 link -o $unsigned -I $androidJar --manifest $manifest --java $gen $flat
if ($LASTEXITCODE -ne 0) { throw "aapt2 link failed" }

$sources = @()
$sources += Get-ChildItem -LiteralPath $sourceRoot -Recurse -Filter *.java | ForEach-Object { $_.FullName }
$sources += Get-ChildItem -LiteralPath $gen -Recurse -Filter *.java | ForEach-Object { $_.FullName }
$argFile = Join-Path $build "sources.txt"
$sources | Set-Content -LiteralPath $argFile -Encoding ascii

& $javac -source 8 -target 8 -encoding UTF-8 -classpath $androidJar -d $classes "@$argFile"
if ($LASTEXITCODE -ne 0) { throw "javac failed" }

$classesJar = Join-Path $build "classes.jar"
if (Test-Path -LiteralPath $classesJar) {
    Remove-Item -LiteralPath $classesJar -Force
}
Push-Location $classes
& $jar cf $classesJar .
Pop-Location
if ($LASTEXITCODE -ne 0) { throw "jar classes failed" }

& $d8 --lib $androidJar --output $dex $classesJar
if ($LASTEXITCODE -ne 0) { throw "d8 failed" }

Push-Location $dex
& $jar uf $unsigned classes.dex
Pop-Location
if ($LASTEXITCODE -ne 0) { throw "adding classes.dex failed" }

$aligned = Join-Path $ProjectRoot "builds\aligned\JoelStream-$stamp-aligned.apk"
New-Item -ItemType Directory -Force -Path (Split-Path $aligned) | Out-Null
& $zipalign -p 4 $unsigned $aligned
if ($LASTEXITCODE -ne 0) { throw "zipalign failed" }

$ks = Join-Path $ProjectRoot "builds\joel-stream-internal.jks"
if (-not (Test-Path -LiteralPath $ks)) {
    & $keytool -genkeypair -v -keystore $ks -storepass "joelstream-internal" -keypass "joelstream-internal" -alias "joelstream" -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Joel Dongthansang, OU=Internal, O=Joel Stream, L=Churachandpur, ST=Manipur, C=IN"
    if ($LASTEXITCODE -ne 0) { throw "keytool failed" }
}

$signed = Join-Path $ProjectRoot "builds\signed\JoelStream-$stamp-signed.apk"
New-Item -ItemType Directory -Force -Path (Split-Path $signed) | Out-Null
& $apksigner sign --ks $ks --ks-key-alias "joelstream" --ks-pass "pass:joelstream-internal" --key-pass "pass:joelstream-internal" --out $signed $aligned
if ($LASTEXITCODE -ne 0) { throw "apksigner sign failed" }

$verifyLog = Join-Path $logs "apksigner_verify_$stamp.txt"
& $apksigner verify --verbose --print-certs $signed *> $verifyLog
if ($LASTEXITCODE -ne 0) { throw "apksigner verify failed" }

$latestDir = Join-Path $ProjectRoot "deliverables\latest"
$testedDir = Join-Path $ProjectRoot "builds\tested"
New-Item -ItemType Directory -Force -Path $latestDir, $testedDir | Out-Null
$latest = Join-Path $latestDir "JoelStream-latest.apk"
$tested = Join-Path $testedDir "JoelStream-$stamp-tested.apk"
Copy-Item -LiteralPath $signed -Destination $latest -Force
Copy-Item -LiteralPath $signed -Destination $tested -Force
$hash = Get-FileHash -LiteralPath $latest -Algorithm SHA256
"$($hash.Hash)  JoelStream-latest.apk" | Set-Content -LiteralPath "$latest.sha256" -Encoding ascii

[pscustomobject]@{
    Unsigned = $unsigned
    Aligned = $aligned
    Signed = $signed
    Tested = $tested
    Latest = $latest
    Sha256 = $hash.Hash
    VerifyLog = $verifyLog
}
