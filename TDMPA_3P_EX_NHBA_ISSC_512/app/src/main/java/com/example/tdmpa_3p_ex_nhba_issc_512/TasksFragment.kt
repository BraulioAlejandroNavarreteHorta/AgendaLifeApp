package com.example.tdmpa_3p_ex_nhba_issc_512

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksFragment : Fragment(), TaskDialogFragment.TaskDialogListener, TaskDialogFragment.OnTaskUpdateListener {
    private lateinit var listView: ListView
    private lateinit var adapter: TaskAdapter
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
        refreshTasks()
    }

    override fun onTaskUpdated() {
        refreshTasks()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)
        val fab_add_task = view.findViewById<FloatingActionButton>(R.id.fab_add_task)

        listView = view.findViewById(R.id.listViewTasks)

        val tareasNoCompletadas = obtenerListaTareas().filter { !it.state }
        adapter = TaskAdapter(requireContext(), R.layout.task_list_item, tareasNoCompletadas.toMutableList(), databaseHelper, { refreshTasks() }, false)
        listView.adapter = adapter

        fab_add_task.setOnClickListener {
            val dialog = TaskDialogFragment(databaseHelper)
            dialog.listener = this
            dialog.taskUpdateListener = this
            dialog.show(parentFragmentManager, "TaskDialogFragment")
        }

        return view
    }

    private fun obtenerListaTareas(): List<TaskModel> {
        return databaseHelper.getAllTasks()
    }

    override fun onTaskAdded() {
        refreshTasks()
    }

    private fun refreshTasks() {
        val updatedTasks = databaseHelper.getAllTasks().filter { !it.state }
        adapter.clear()
        adapter.addAll(updatedTasks)
        adapter.notifyDataSetChanged()
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TasksFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
