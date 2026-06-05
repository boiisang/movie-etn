package com.joel.stream;

final class Catalog {
    private Catalog() {}

    static final MediaItem[] ITEMS = new MediaItem[] {
        new MediaItem(
            "Sintel Trailer",
            "Open Movie",
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
