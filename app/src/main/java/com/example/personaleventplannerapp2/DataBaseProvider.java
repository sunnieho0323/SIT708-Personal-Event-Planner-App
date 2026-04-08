package com.example.personaleventplannerapp2;

import android.content.Context;
import androidx.room.Room;

public class DataBaseProvider {
    private static AppDataBase instance;

    public static synchronized AppDataBase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDataBase.class,
                    "event_planner_db"
            ).build();
        }
        return instance;
    }
}