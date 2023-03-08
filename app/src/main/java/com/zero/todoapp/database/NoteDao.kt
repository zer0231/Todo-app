package com.zero.todoapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.zero.todoapp.models.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(note: Note)

    @Delete()
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes_table ORDER BY id ASC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("UPDATE notes_table SET title = :title, body= :body WHERE id= :id")
    suspend fun update(id: Int?, title: String?, body: String?)
}