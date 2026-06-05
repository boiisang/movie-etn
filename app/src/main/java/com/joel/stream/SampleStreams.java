package com.joel.stream;

final class SampleStreams {
    private static final String[] URLS = new String[] {
        "https://media.w3.org/2010/05/sintel/trailer.mp4",
        "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
        "https://media.w3.org/2010/05/bunny/trailer.mp4",
        "https://media.w3.org/2010/05/video/movie_300.mp4",
        "https://download.samplelib.com/mp4/sample-5s.mp4",
        "https://download.samplelib.com/mp4/sample-10s.mp4"
    };

    private SampleStreams() {}

    static String forIndex(int index) {
        return URLS[Math.abs(index) % URLS.length];
    }

    static String[] fallbacks(String preferred) {
        if (preferred != null && preferred.trim().length() > 0) {
            return new String[] { preferred.trim() };
        }
        return new String[] {
            URLS[0],
            URLS[1],
            URLS[2],
            URLS[3],
            URLS[4]
        };
    }
}
