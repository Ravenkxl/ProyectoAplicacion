package co.edu.uis.organizationapp.vista.calendario;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// Adaptador para LocalDate
class LocalDateAdapter extends TypeAdapter<LocalDate> {
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        out.value(value != null ? value.toString() : null);
    }
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String s = in.nextString();
        return s != null ? LocalDate.parse(s) : null;
    }
}

// Adaptador para LocalTime
class LocalTimeAdapter extends TypeAdapter<LocalTime> {
    @Override
    public void write(JsonWriter out, LocalTime value) throws IOException {
        out.value(value != null ? value.toString() : null);
    }
    @Override
    public LocalTime read(JsonReader in) throws IOException {
        String s = in.nextString();
        return s != null ? LocalTime.parse(s) : null;
    }
}

// Adaptador para LocalDateTime
class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        out.value(value != null ? value.toString() : null);
    }
    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        String s = in.nextString();
        return s != null ? LocalDateTime.parse(s) : null;
    }
}
