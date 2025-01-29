package com.ll.server.domain.news.news.enums;

public enum NewsCategory {
    POLITICS("politics"),
    ECONOMY("economy"),
    SOCIETY("society"),
    CULTURE("culture"),
    WORLD("world"),
    TECH("tech"),
    ENTERTAINMENT("entertainment"),
    OPINION("opinion");

    private final String category;

    NewsCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
