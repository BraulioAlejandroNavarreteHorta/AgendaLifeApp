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

class TaskAdapter(
    private val context: Context,
    private val layoutResId: Int,
    private var tasks: MutableList<TaskModel>,
    private val databaseHelper: DatabaseHelper,
    private val refreshTasks: () -> Unit,
    private val isCompleteList: Boolean
) : ArrayAdapter<TaskModel>(context, layoutResId, tasks),
    TaskDialogFragment.TaskDialogListener, TaskDialogFragment.OnTaskUpdateListener {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutResId, null)

        var task = getItem(position) ?: return view

        val tvTaskName = view.findViewById<TextView>(R.id.tvTaskName)
        val tvTaskDate = view.findViewById<TextView>(R.id.tvTaskDate)
        val tvTaskStatus = view.findViewById<TextView>(R.id.tvTaskStatus)
        val btnEditTask = view.findViewById<ImageButton>(R.id.btnEditTask)
        val btnDeleteTask = view.findViewById<ImageButton>(R.id.btnDeleteTask)
        val btnCompleteTask = view.findViewById<ImageButton>(R.id.btnCompleteTask)

        tvTaskName.text = task.name
        tvTaskDate.text = task.reminder
        tvTaskStatus.text = if (task.state) "Completado" else "Pendiente"

        if (isCompleteList) {
            btnEditTask.visibility = View.GONE
            btnDeleteTask.visibility = View.GONE
            btnCompleteTask.visibility = View.GONE
        } else {
            btnEditTask.setOnClickListener {
                val editDialog = TaskDialogFragment(databaseHelper, task)
                editDialog.listener = this
                editDialog.taskUpdateListener = this
                editDialog.show((context as FragmentActivity).supportFragmentManager, "EditTaskDialog")
            }

            if (task.state) {
                tvTaskName.paintFlags = tvTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvTaskDate.paintFlags = tvTaskDate.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvTaskStatus.paintFlags = tvTaskStatus.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                btnCompleteTask.visibility = View.INVISIBLE
                btnEditTask.visibility = View.INVISIBLE
            } else {
                tvTaskName.paintFlags = tvTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvTaskDate.paintFlags = tvTaskDate.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvTaskStatus.paintFlags = tvTaskStatus.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                btnCompleteTask.visibility = View.VISIBLE
                btnEditTask.visibility = View.VISIBLE
            }

            btnDeleteTask.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Confirmar Eliminación")
                    .setMessage("¿Estás seguro de querer eliminar esta tarea?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        if (task.state) {
                            tasks.removeAt(position)
                            notifyDataSetChanged()
                        } else {
                            databaseHelper.deleteTask(task.id)
                            refreshTasks()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            btnCompleteTask.setOnClickListener {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                task.reminder = currentDate
                task.state = true

                // Actualizar el evento en la base de datos como completado
                databaseHelper.updateTask(task)

                // Añadir el evento a la lista de completados
                val completedItem = CompletedItemModel(task.id, "note", task.name, task.reminder)
                databaseHelper.addCompletedItem(completedItem)

                // Eliminar el evento de la lista visible y notificar al adaptador
                tasks.removeAt(position)
                notifyDataSetChanged()
                refreshTasks() // Asegúrate de que este método actualice la lista correctamente
            }
        }

        return view
    }

    override fun onTaskAdded() {
        refreshTasks()
    }

    override fun onTaskUpdated() {
        refreshTasks()
    }
}
