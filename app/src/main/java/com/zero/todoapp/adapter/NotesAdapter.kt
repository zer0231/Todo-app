package com.zero.todoapp.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zero.todoapp.R
import com.zero.todoapp.models.Note

class NotesAdapter(private val context: Context, private val listener: NoteClickListener) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val fullList = ArrayList<Note>()
    private val notesList = ArrayList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesAdapter.NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotesAdapter.NoteViewHolder, position: Int) {
        val currentNote = notesList[position]
        holder.title.text =
            currentNote.title //The title property can be accessed only after declared in NoteViewHolder inner class
        holder.title.isSelected = true
        holder.body.text = currentNote.body
        holder.date.text = currentNote.date
        holder.date.isSelected = true
        holder.notesLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.orange))

        holder.notesLayout.setOnClickListener {
            listener.onItemClicked(notesList[holder.adapterPosition])
        }
        holder.notesLayout.setOnClickListener {
            listener.onItemLongClicked(notesList[holder.adapterPosition], holder.notesLayout)
//            true
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    fun updateList(newList: List<Note>) {
        fullList.clear()
        fullList.addAll(newList)
        notesList.clear()
        notesList.addAll(fullList)
        notifyDataSetChanged()
    }

    fun filterList(query: String) {
        notesList.clear()
        for (item in fullList) {
            if (item.title?.lowercase()
                    ?.contains(query.lowercase()) == true || item.body?.lowercase()
                    ?.contains(query.lowercase()) == true || item.date?.lowercase()
                    ?.contains(query.lowercase()) == true
            )
                notesList.add(item)
        }
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notesLayout: CardView = itemView.findViewById<CardView>(R.id.note_card_view)
        val title: TextView = itemView.findViewById<TextView>(R.id.card_title)
        val body: TextView = itemView.findViewById<TextView>(R.id.card_body)
        val date: TextView = itemView.findViewById<TextView>(R.id.card_date)
    }

    interface NoteClickListener {
        fun onItemClicked(note: Note)
        fun onItemLongClicked(note: Note, cardView: CardView)
    }
}