package com.example.personaleventplannerapp2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface EventDao {
    // Insert a new record
    @Insert
    void insert(Event event);

    // Update an existing record
    @Update
    void update(Event event);

    // Delete a record
    @Delete
    void delete(Event event);

    // Get a single event for editing
    @Query("SELECT * FROM events WHERE id = :id")
    Event getEventById(long id);

    // Get all events for the list
    @Query("SELECT * FROM events ORDER BY dateTime ASC")
    LiveData<List<Event>> getAllEvents();
}