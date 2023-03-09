package com.zero.todoapp


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zero.todoapp.databinding.ActivityAddNoteBinding
import com.zero.todoapp.models.Note
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var note: Note
    private lateinit var oldNote: Note
    var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            oldNote = intent.getSerializableExtra("current_note") as Note
            binding.etNoteTitle.setText(oldNote.title)
            binding.etNoteBody.setText(oldNote.body)
            isUpdate = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.addNoteCheck.setOnClickListener {
            val title = binding.etNoteTitle.text.toString()
            val body = binding.etNoteBody.text.toString()
            if (title.isNotEmpty()) {
                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:MM a")
                if (isUpdate) {
                    note = Note(oldNote.id, title, body, formatter.format(Date()))
                } else {
                    note = Note(null, title, body, formatter.format(Date()))
                }
                val intent = Intent(intent.putExtra("note", note))
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
            }

        }
        binding.addNoteBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}