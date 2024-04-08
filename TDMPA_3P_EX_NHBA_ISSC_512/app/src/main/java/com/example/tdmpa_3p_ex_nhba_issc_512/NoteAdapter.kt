package com.example.tdmpa_3p_ex_nhba_issc_512

import android.app.AlertDialog
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(
    private val context: Context,
    private val layoutResId: Int,
    private var notes: MutableList<NoteModel>,
    private val databaseHelper: DatabaseHelper,
    private val refreshNotes: () -> Unit,
    private val isCompleteList: Boolean
) : ArrayAdapter<NoteModel>(context, layoutResId, notes),
    NoteDialogFragment.NoteDialogListener, NoteDialogFragment.OnNoteUpdateListener {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutResId, null)

        var note = getItem(position) ?: return view

        val tvNoteName = view.findViewById<TextView>(R.id.tvNoteName)
        val tvNoteDate = view.findViewById<TextView>(R.id.tvNoteDate)
        val tvNoteStatus = view.findViewById<TextView>(R.id.tvNoteStatus)
        val btnEditNote = view.findViewById<ImageButton>(R.id.btnEditNote)
        val btnDeleteNote = view.findViewById<ImageButton>(R.id.btnDeleteNote)
        val btnCompleteNote = view.findViewById<ImageButton>(R.id.btnCompleteNote)

        tvNoteName.text = note.name
        tvNoteDate.text = note.reminder
        tvNoteStatus.text = if (note.state) "Completado" else "Pendiente"

        if (isCompleteList) {
            btnEditNote.visibility = View.GONE
            btnDeleteNote.visibility = View.GONE
            btnCompleteNote.visibility = View.GONE
        } else {
            btnEditNote.setOnClickListener {
                val editDialog = NoteDialogFragment(databaseHelper, note)
                editDialog.listener = this
                editDialog.noteUpdateListener = this
                editDialog.show((context as FragmentActivity).supportFragmentManager, "EditNoteDialog")
            }

            if (note.state) {
                tvNoteName.paintFlags = tvNoteName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvNoteDate.paintFlags = tvNoteDate.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvNoteStatus.paintFlags = tvNoteStatus.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                btnCompleteNote.visibility = View.INVISIBLE
                btnEditNote.visibility = View.INVISIBLE
            } else {
                tvNoteName.paintFlags = tvNoteName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvNoteDate.paintFlags = tvNoteDate.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvNoteStatus.paintFlags = tvNoteStatus.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                btnCompleteNote.visibility = View.VISIBLE
                btnEditNote.visibility = View.VISIBLE
            }

            btnDeleteNote.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Confirmar Eliminación")
                    .setMessage("¿Estás seguro de querer eliminar esta nota?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        if (note.state) {
                            notes.removeAt(position)
                            notifyDataSetChanged()
                        } else {
                            databaseHelper.deleteNote(note.id)
                            refreshNotes()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            btnCompleteNote.setOnClickListener {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                note.reminder = currentDate
                note.state = true

                // Actualizar el evento en la base de datos como completado
                databaseHelper.updateNote(note)

                // Añadir el evento a la lista de completados
                val completedItem = CompletedItemModel(note.id, "note", note.name, note.reminder)
                databaseHelper.addCompletedItem(completedItem)

                // Eliminar el evento de la lista visible y notificar al adaptador
                notes.removeAt(position)
                notifyDataSetChanged()
                refreshNotes() // Asegúrate de que este método actualice la lista correctamente
            }
        }

        return view
    }

    override fun onNoteAdded() {
        refreshNotes()
    }

    override fun onNoteUpdated() {
        refreshNotes()
    }
}
