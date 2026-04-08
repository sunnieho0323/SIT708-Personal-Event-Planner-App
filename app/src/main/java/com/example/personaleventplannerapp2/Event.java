package com.example.personaleventplannerapp2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Entity(tableName = "events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    public long id = 0;

    public String title;
    public String category;
    public String location;
    public String dateTime;      //  "2026-04-15 14:30"
    public long createdAt = System.currentTimeMillis();

    public Event(String title, String category, String location, String dateTime) {
        this.title = title;
        this.category = category;
        this.location = location;
        this.dateTime = dateTime;
    }

    public static final SimpleDateFormat formatter =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
}