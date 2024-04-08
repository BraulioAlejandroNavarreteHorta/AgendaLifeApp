package com.example.tdmpa_3p_ex_nhba_issc_512

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CompleteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompleteFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var adapter: CompletedItemAdapter
    private lateinit var databaseHelper: DatabaseHelper
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_complete, container, false)
        listView = view.findViewById(R.id.listViewCompletes)
        databaseHelper = DatabaseHelper(requireContext())

        val completedItems = obtenerItemsCompletados()
        adapter = CompletedItemAdapter(requireContext(), R.layout.complete_list_item, completedItems, databaseHelper)


        listView.adapter = adapter

        return view
    }

    private fun obtenerItemsCompletados(): MutableList<CompletedItemModel> {
        val completedItems = mutableListOf<CompletedItemModel>()

        // Agregar eventos completados
        completedItems.addAll(databaseHelper.getAllEvents().filter { it.state }
            .map { CompletedItemModel(it.id, "event", it.name, it.reminder) })

        // Agregar notas completadas
        completedItems.addAll(databaseHelper.getAllNotes().filter { it.state }
            .map { CompletedItemModel(it.id, "note", it.name, it.reminder) })

        // Agregar tareas completadas

        completedItems.addAll(databaseHelper.getAllTasks().filter { it.state }
            .map { CompletedItemModel(it.id, "task", it.name, it.reminder) })


        return completedItems
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CompleteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CompleteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}