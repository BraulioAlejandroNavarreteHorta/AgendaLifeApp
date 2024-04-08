package com.example.tdmpa_3p_ex_nhba_issc_512

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotesFragment : Fragment(), NoteDialogFragment.NoteDialogListener, NoteDialogFragment.OnNoteUpdateListener {
    private lateinit var listView: ListView
    private lateinit var adapter: NoteAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(requireContext())
        if (arguments != null) {
            var param1 = requireArguments().getString(ARG_PARAM1)
            var param2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshNotes()
    }

    override fun onNoteUpdated() {
        refreshNotes()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)
        val fab_add_note = view.findViewById<FloatingActionButton>(R.id.fab_add_note)

        listView = view.findViewById(R.id.listViewNotes)

        val notasNoCompletadas = obtenerListaNotas().filter { !it.state }
        adapter = NoteAdapter(requireContext(), R.layout.note_list_item, notasNoCompletadas.toMutableList(), databaseHelper, { refreshNotes() }, false)
        listView.adapter = adapter

        fab_add_note.setOnClickListener {
            val dialog = NoteDialogFragment(databaseHelper)
            dialog.listener = this
            dialog.noteUpdateListener = this
            dialog.show(parentFragmentManager, "NoteDialogFragment")
        }

        return view
    }

    private fun obtenerListaNotas(): List<NoteModel> {
        return databaseHelper.getAllNotes()
    }

    override fun onNoteAdded() {
        refreshNotes()
    }

    private fun refreshNotes() {
        val updatedNotes = databaseHelper.getAllNotes().filter { !it.state }
        adapter.clear()
        adapter.addAll(updatedNotes)
        adapter.notifyDataSetChanged()
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
