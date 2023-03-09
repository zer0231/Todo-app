package com.zero.todoapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.zero.todoapp.adapter.NotesAdapter
import com.zero.todoapp.database.NoteDatabase
import com.zero.todoapp.databinding.ActivityMainBinding
import com.zero.todoapp.models.Note
import com.zero.todoapp.models.NoteViewModel

class MainActivity : AppCompatActivity(), NotesAdapter.NoteClickListener,
    PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: ActivityMainBinding //Declare binding for view binding
    private lateinit var database: NoteDatabase //Declare database object
    lateinit var selectedNote: Note
    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NotesAdapter

    private val updateNote =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val note = result.data?.getSerializableExtra("note") as? Note
                if (note != null) {
                    if (note.id == null) {
                        viewModel.insertNote(note)
                    }
                    else{
                        viewModel.updateNote(note)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializing UI
        initUI()

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModel::class.java)
        viewModel.allNotes.observe(this) { list ->
            list?.let { adapter.updateList(list) }
        }
        database = NoteDatabase.getDatabase(this)
    }

    private fun initUI() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapter = NotesAdapter(this, this)
        binding.recyclerView.adapter = adapter
        //registering getContent so that when the user taps on floating action button and create new note then we get the result from that activity
        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val note = result.data?.getSerializableExtra("note") as? Note
                    if (note != null) {
                        viewModel.insertNote(note)
                    }
                }
            }
        binding.addFloatingActionBtn.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            getContent.launch(intent)
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    adapter.filterList(query)
                }
                return true
            }

        })

    }

    override fun onItemClicked(note: Note) {
        val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
        intent.putExtra("current_note", note)
        updateNote.launch(intent)
    }

    override fun onLongItemClicked(note: Note, cardView: CardView) {
        selectedNote = note
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView) {
        val popup = PopupMenu(this, cardView)
        popup.setOnMenuItemClickListener(this@MainActivity)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_note) {
            viewModel.deleteNote(selectedNote)
            return true
        }
        return false
    }
}