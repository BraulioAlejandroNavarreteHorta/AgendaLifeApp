package com.example.tdmpa_3p_ex_nhba_issc_512

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class NoteDialogFragment(
    private val databaseHelper: DatabaseHelper,
    private val existingNote: NoteModel? = null
) : DialogFragment() {

    interface NoteDialogListener {
        fun onNoteAdded()
    }

    interface OnNoteUpdateListener {
        fun onNoteUpdated()
    }

    var listener: NoteDialogListener? = null
    var noteUpdateListener: OnNoteUpdateListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_note_form, null)

            existingNote?.let { note ->
                dialogView.findViewById<EditText>(R.id.edit_note_name).setText(note.name)
                dialogView.findViewById<EditText>(R.id.edit_note_date).setText(note.reminder)
            }

            builder.setView(dialogView)
                .setPositiveButton("Guardar") { _, _ ->
                    val noteName = dialogView.findViewById<EditText>(R.id.edit_note_name).text.toString()
                    val noteDate = dialogView.findViewById<EditText>(R.id.edit_note_date).text.toString()

                    if (noteName.isNotEmpty() && noteDate.isNotEmpty()) {
                        if (existingNote != null) {
                            existingNote.name = noteName
                            existingNote.reminder = noteDate
                            databaseHelper.updateNote(existingNote)
                            listener?.onNoteAdded()
                            noteUpdateListener?.onNoteUpdated()
                        } else {
                            val newNote = NoteModel(0, noteName, noteDate, false)
                            databaseHelper.addNote(newNote)
                        }
                        listener?.onNoteAdded()
                        noteUpdateListener?.onNoteUpdated()
                    } else {
                        // Manejar el caso de datos inválidos
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }

            val noteDateEditText = dialogView.findViewById<EditText>(R.id.edit_note_date)
            noteDateEditText.setOnClickListener {
                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(it.context, { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
                    noteDateEditText.setText(selectedDate)
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                datePickerDialog.show()
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
