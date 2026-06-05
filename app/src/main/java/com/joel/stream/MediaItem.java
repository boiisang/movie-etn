package com.joel.stream;

final class MediaItem {
    final String title;
    final String category;
    final String year;
    final String duration;
    final String description;
    final String videoUrl;
    final int colorA;
    final int colorB;

    MediaItem(String title, String category, String year, String duration, String description, String videoUrl, int colorA, int colorB) {
        this.title = title;
        this.category = category;
        this.year = year;
        this.duration = duration;
        this.description = description;
        this.videoUrl = videoUrl;
        this.colorA = colorA;
        this.colorB = colorB;
    }
}
