package com.example.tdmpa_3p_ex_nhba_issc_512

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EventsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventsFragment : Fragment(), EventDialogFragment.EventDialogListener, EventDialogFragment.OnEventUpdateListener {
    private lateinit var listView: ListView
    private lateinit var adapter: EventAdapter
    private lateinit var databaseHelper: DatabaseHelper

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(requireContext())
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshEvents() // Refresca los eventos cada vez que el fragment se muestra
    }

    override fun onEventUpdated() {
        refreshEvents()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_events, container, false)
        val fab_add_event = view.findViewById<FloatingActionButton>(R.id.fab_add_event)

        listView = view.findViewById(R.id.listViewEvents)

        val eventosNoCompletados = obtenerListaEventos().filter { !it.state }
        adapter = EventAdapter(requireContext(), R.layout.even_list_item, eventosNoCompletados.toMutableList(), databaseHelper, { refreshEvents() }, false)
        listView.adapter = adapter

        fab_add_event.setOnClickListener {
            val dialog = EventDialogFragment(databaseHelper)
            dialog.listener = this // Establecer el listener para agregar eventos
            dialog.eventUpdateListener = this // Establecer el listener para actualizar eventos
            dialog.show(parentFragmentManager, "EventDialogFragment")
        }

        return view
    }

    private fun obtenerListaEventos(): List<EventModel> {
        // Aquí deberías obtener tus datos de la base de datos
        return databaseHelper.getAllEvents()

    }

    override fun onEventAdded() {
        // Actualizar la lista con eventos no completados
        refreshEvents()
    }
    // Método para actualizar la lista visible
    private fun refreshEvents() {
        val updatedEvents = databaseHelper.getAllEvents().filter { !it.state }
        adapter.clear()
        adapter.addAll(updatedEvents)
        adapter.notifyDataSetChanged()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EventsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



}