package com.example.tdmpa_3p_ex_nhba_issc_512

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import java.util.Calendar


class EventDialogFragment(
    private val databaseHelper: DatabaseHelper,
    private val existingEvent: EventModel? = null
) : DialogFragment() {

    interface EventDialogListener {
        fun onEventAdded()
    }

    interface OnEventUpdateListener {
        fun onEventUpdated()
    }

    var listener: EventDialogListener? = null

    var eventUpdateListener: OnEventUpdateListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_event_form, null)

            existingEvent?.let { event ->
                dialogView.findViewById<EditText>(R.id.edit_event_name).setText(event.name)
                dialogView.findViewById<EditText>(R.id.edit_event_date).setText(event.reminder)
            }

            builder.setView(dialogView)
                .setPositiveButton("Guardar") { _, _ ->
                    val eventName = dialogView.findViewById<EditText>(R.id.edit_event_name).text.toString()
                    val eventDate = dialogView.findViewById<EditText>(R.id.edit_event_date).text.toString()

                    if (eventName.isNotEmpty() && eventDate.isNotEmpty()) {
                        if (existingEvent != null) {
                            existingEvent.name = eventName
                            existingEvent.reminder = eventDate
                            databaseHelper.updateEvent(existingEvent)
                            listener?.onEventAdded()
                            eventUpdateListener?.onEventUpdated()
                        } else {
                            val newEvent = EventModel(0, eventName, eventDate, false)
                            databaseHelper.addEvent(newEvent)
                        }
                        listener?.onEventAdded()
                        eventUpdateListener?.onEventUpdated()
                    } else {
                        // Manejar el caso de datos inválidos
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }

            val eventDateEditText = dialogView.findViewById<EditText>(R.id.edit_event_date)
            eventDateEditText.setOnClickListener {
                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(it.context, { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
                    eventDateEditText.setText(selectedDate)
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                datePickerDialog.show()
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
