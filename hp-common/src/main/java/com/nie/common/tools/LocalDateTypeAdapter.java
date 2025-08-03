package com.nie.common.tools;


import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value != null) {
            out.value(value.format(formatter));
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String dateStr = in.nextString();
        return LocalDate.parse(dateStr, formatter);
    }
}