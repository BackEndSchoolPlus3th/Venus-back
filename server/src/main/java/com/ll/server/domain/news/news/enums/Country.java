package com.ll.server.domain.news.news.enums;


public enum Country {
    // Americas
    USA("United States"),
    CAN("Canada"),
    BRA("Brazil"),
    MEX("Mexico"),
    ARG("Argentina"),
    CHL("Chile"),
    COL("Colombia"),

    // Europe
    DEU("Germany"),
    FRA("France"),
    GBR("United Kingdom"),
    ITA("Italy"),
    ESP("Spain"),
    RUS("Russia"),
    NLD("Netherlands"),
    SWE("Sweden"),

    // Asia
    CHN("China"),
    JPN("Japan"),
    KOR("South Korea"),
    IND("India"),
    IDN("Indonesia"),
    PAK("Pakistan"),
    TUR("Turkey"),

    // Oceania
    AUS("Australia"),
    NZL("New Zealand"),
    FJI("Fiji"),

    // Africa
    ZAF("South Africa"),
    EGY("Egypt"),
    NGA("Nigeria"),
    KEN("Kenya"),
    ETH("Ethiopia"),

    // Middle East
    SAU("Saudi Arabia"),
    IRN("Iran"),
    ISR("Israel"),
    UAE("United Arab Emirates");

    private final String fullName;

    Country(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public static Country fromCode(String code) {
        for (Country country : Country.values()) {
            if (country.name().equalsIgnoreCase(code)) {
                return country;
            }
        }
        throw new IllegalArgumentException("Invalid country code: " + code);
    }

    @Override
    public String toString() {
        return name() + " (" + fullName + ")";
    }
}
