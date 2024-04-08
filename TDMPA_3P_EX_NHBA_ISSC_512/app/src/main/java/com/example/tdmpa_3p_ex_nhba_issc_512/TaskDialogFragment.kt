package com.example.tdmpa_3p_ex_nhba_issc_512

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TaskDialogFragment(
    private val databaseHelper: DatabaseHelper,
    private val existingTask: TaskModel? = null
) : DialogFragment() {

    interface TaskDialogListener {
        fun onTaskAdded()
    }

    interface OnTaskUpdateListener {
        fun onTaskUpdated()
    }

    var listener: TaskDialogListener? = null
    var taskUpdateListener: OnTaskUpdateListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_task_form, null)

            existingTask?.let { task ->
                dialogView.findViewById<EditText>(R.id.edit_task_name).setText(task.name)
                dialogView.findViewById<EditText>(R.id.edit_task_date).setText(task.reminder)
            }

            builder.setView(dialogView)
                .setPositiveButton("Guardar") { _, _ ->
                    val taskName = dialogView.findViewById<EditText>(R.id.edit_task_name).text.toString()
                    val taskDate = dialogView.findViewById<EditText>(R.id.edit_task_date).text.toString()

                    if (taskName.isNotEmpty() && taskDate.isNotEmpty()) {
                        if (existingTask != null) {
                            existingTask.name = taskName
                            existingTask.reminder = taskDate
                            databaseHelper.updateTask(existingTask)
                            listener?.onTaskAdded()
                            taskUpdateListener?.onTaskUpdated()
                        } else {
                            val newTask = TaskModel(0, taskName, taskDate, false)
                            databaseHelper.addTask(newTask)
                        }
                        listener?.onTaskAdded()
                        taskUpdateListener?.onTaskUpdated()
                    } else {
                        // Handle invalid data case
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }

            val taskDateEditText = dialogView.findViewById<EditText>(R.id.edit_task_date)
            taskDateEditText.setOnClickListener {
                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(it.context, { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
                    taskDateEditText.setText(selectedDate)
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                datePickerDialog.show()
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
