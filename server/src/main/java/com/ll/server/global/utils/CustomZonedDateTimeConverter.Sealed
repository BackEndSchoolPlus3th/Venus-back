package com.ll.server.global.utils;

import org.springframework.data.elasticsearch.core.mapping.PropertyValueConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CustomZonedDateTimeConverter implements PropertyValueConverter {

    private final DateTimeFormatter formatterWithZone = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSSSSX");
    private final DateTimeFormatter formatterWithoutZone = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSSSS");

    @Override
    public Object write(Object value) {
        if (value instanceof ZonedDateTime zonedDateTime) {
            return formatterWithZone.format(zonedDateTime);
        } else {
            return value;
        }
    }

    @Override
    public Object read(Object value) {
        if (value instanceof String s) {
            try {
                return formatterWithZone.parse(s, ZonedDateTime::from);
            } catch (Exception e) {
                return formatterWithoutZone.parse(s, LocalDateTime::from).atZone(ZoneId.of("Asia/Seoul"));
            }
        } else {
            return value;
        }
    }
}