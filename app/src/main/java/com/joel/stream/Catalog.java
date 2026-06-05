package com.joel.stream;

final class Catalog {
    private Catalog() {}

    static final MediaItem[] ITEMS = new MediaItem[] {
        new MediaItem(
            "Big Buck Bunny",
            "Watchable Movie",
            "2008",
            "10 min",
            "A Creative Commons open movie from the Blender open movie project, encoded for reliable Android playback.",
            "https://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4",
            0xfff25f4c,
            0xff5cc8ff
        ),
        new MediaItem(
            "Big Buck Bunny Trailer",
            "Open Movie",
            "2008",
            "30 sec",
            "A compact Creative Commons trailer for quick playback checks.",
            "https://media.w3.org/2010/05/bunny/trailer.mp4",
            0xff3ddc97,
            0xff2077ff
        ),
        new MediaItem(
            "Elephants Dream",
            "Open Movie",
            "2006",
            "11 min",
            "A Creative Commons open movie from the Blender Foundation.",
            "https://archive.org/download/ElephantsDream/ed_hd_512kb.mp4",
            0xff7d6cff,
            0xffff6ca8
        ),
        new MediaItem(
            "Sintel",
            "Open Movie",
            "2010",
            "15 min",
            "A Creative Commons open fantasy short from Blender's Durian project.",
            "https://archive.org/download/sintel-open-movie-by-blender/Sintel%20-%20Open%20Movie%20by%20Blender.mp4",
            0xffffc857,
            0xff3b3b98
        ),
        new MediaItem(
            "Cosmos Laundromat",
            "Open Movie",
            "2015",
            "12 min",
            "A Creative Commons Blender open movie, First Cycle.",
            "https://archive.org/download/CosmosLaundromatFirstCycle/Cosmos%20Laundromat%20-%20First%20Cycle%20%281080p%29.mp4",
            0xffff7a59,
            0xff202124
        ),
        new MediaItem(
            "Caminandes: Llama Drama",
            "Open Series",
            "2013",
            "2 min",
            "Episode one of Blender's Creative Commons Caminandes animated shorts.",
            "https://archive.org/download/Caminandes1LlamaDrama/01_llama_drama_1080p.mp4",
            0xff64d2ff,
            0xff2d3436
        ),
        new MediaItem(
            "Caminandes: Gran Dillama",
            "Open Series",
            "2013",
            "3 min",
            "Episode two of Blender's Creative Commons Caminandes animated shorts.",
            "https://archive.org/download/Caminandes2GranDillama/02_gran_dillama_1080p.mp4",
            0xffb8f35a,
            0xff1f2937
        ),
        new MediaItem(
            "Sintel Trailer",
            "Sample Short",
            "2010",
            "1 min",
            "A short open movie trailer hosted by W3C media examples.",
            "https://media.w3.org/2010/05/sintel/trailer.mp4",
            0xfff25f4c,
            0xff5cc8ff
        ),
        new MediaItem(
            "Flower Garden",
            "CC0 Sample",
            "Demo",
            "5 sec",
            "A lightweight CC0 sample clip for fast playback validation.",
            "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
            0xff7d6cff,
            0xffff6ca8
        ),
        new MediaItem(
            "Big Bunny Trailer",
            "Open Movie",
            "2008",
            "30 sec",
            "A compact open movie trailer for stable streaming tests.",
            "https://media.w3.org/2010/05/bunny/trailer.mp4",
            0xff3ddc97,
            0xff2077ff
        ),
        new MediaItem(
            "W3C Short",
            "Sample Short",
            "Demo",
            "10 sec",
            "A direct MP4 sample clip used for Android playback checks.",
            "https://media.w3.org/2010/05/video/movie_300.mp4",
            0xffffc857,
            0xff3b3b98
        ),
        new MediaItem(
            "Sample Five",
            "Sample Short",
            "Demo",
            "5 sec",
            "A small MP4 sample for quick player startup verification.",
            "https://download.samplelib.com/mp4/sample-5s.mp4",
            0xffff7a59,
            0xff202124
        ),
        new MediaItem(
            "Sample Ten",
            "Sample Short",
            "Demo",
            "10 sec",
            "A short direct MP4 fallback sample for network and player validation.",
            "https://download.samplelib.com/mp4/sample-10s.mp4",
            0xff64d2ff,
            0xff2d3436
        ),
        new MediaItem(
            "Sample File",
            "Sample Short",
            "Demo",
            "Short",
            "A direct sample MP4 that keeps playback independent from monetization services.",
            "https://filesamples.com/samples/video/mp4/sample_640x360.mp4",
            0xffb8f35a,
            0xff1f2937
        ),
        new MediaItem(
            "Sintel Backup",
            "Sample Short",
            "Demo",
            "1 min",
            "Another direct MP4 entry for repeat playback checks.",
            "https://media.w3.org/2010/05/sintel/trailer.mp4",
            0xfff48fb1,
            0xff263238
        )
    };

    static MediaItem findByTitle(String title) {
        if (title == null) return ITEMS[0];
        for (MediaItem item : ITEMS) {
            if (item.title.equals(title)) return item;
        }
        return ITEMS[0];
    }
}
