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

class EventAdapter(
    private val context: Context,
    private val layoutResId: Int,
    private var events: MutableList<EventModel>,
    private val databaseHelper: DatabaseHelper,
    private val refreshEvents: () -> Unit,
    private val isCompleteList: Boolean // Nuevo parámetro para identificar si es lista de completados
) : ArrayAdapter<EventModel>(context, layoutResId, events),
    EventDialogFragment.EventDialogListener, EventDialogFragment.OnEventUpdateListener {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutResId, null)

        var event = getItem(position) ?: return view

        val tvEventName = view.findViewById<TextView>(R.id.tvEventName)
        val tvEventDate = view.findViewById<TextView>(R.id.tvEventDate)
        val tvEventStatus = view.findViewById<TextView>(R.id.tvEventStatus)
        val btnEdit = view.findViewById<ImageButton>(R.id.btnEdit)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)
        val btnComplete = view.findViewById<ImageButton>(R.id.btnComplete)

        tvEventName.text = event.name
        tvEventDate.text = event.reminder
        tvEventStatus.text = if (event.state) "Completado" else "Pendiente"

        if (isCompleteList) {
            // Oculta los botones si es la lista de eventos completados
            btnEdit.visibility = View.GONE
            btnDelete.visibility = View.GONE
            btnComplete.visibility = View.GONE
        } else {
            // Lógica para los botones de editar, eliminar y completar
            // y similarmente cuando se llama al diálogo para editar un evento
            btnEdit.setOnClickListener {
                val editDialog = EventDialogFragment(databaseHelper, event)
                editDialog.listener = this // Establecer el listener para agregar eventos
                editDialog.eventUpdateListener = this // Establecer el listener para actualizar eventos
                editDialog.show((context as FragmentActivity).supportFragmentManager, "EditEventDialog")
            }

            if (event.state) {
                // Tachar el texto si el evento está completado
                tvEventName.paintFlags = tvEventName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvEventDate.paintFlags = tvEventDate.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvEventStatus.paintFlags = tvEventStatus.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                btnComplete.visibility = View.INVISIBLE
                btnEdit.visibility = View.INVISIBLE
            } else {
                // Eliminar el tachado si el evento no está completado
                tvEventName.paintFlags = tvEventName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvEventDate.paintFlags = tvEventDate.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvEventStatus.paintFlags = tvEventStatus.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                btnComplete.visibility = View.VISIBLE
                btnEdit.visibility = View.VISIBLE
            }

            btnDelete.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Confirmar Eliminación")
                    .setMessage("¿Estás seguro de querer eliminar este evento?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        if (event.state) {
                            // Si el evento está completado, elimínalo solo de la lista.
                            events.removeAt(position)
                            notifyDataSetChanged()
                        } else {
                            // Si el evento no está completado, elimínalo de la base de datos.
                            databaseHelper.deleteEvent(event.id)
                            refreshEvents()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            btnComplete.setOnClickListener {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                event.reminder = currentDate
                event.state = true

                // Actualizar el evento en la base de datos como completado
                databaseHelper.updateEvent(event)

                // Añadir el evento a la lista de completados
                val completedItem = CompletedItemModel(event.id, "event", event.name, event.reminder)
                databaseHelper.addCompletedItem(completedItem)

                // Eliminar el evento de la lista visible y notificar al adaptador
                events.removeAt(position)
                notifyDataSetChanged()
                refreshEvents() // Asegúrate de que este método actualice la lista correctamente
            }
            /*
            btnComplete.setOnClickListener {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                event.reminder = currentDate
                event.state = true
                databaseHelper.updateEvent(event)

                // Eliminar el evento de la lista visible y notificar al adapter
                events.removeAt(position)
                notifyDataSetChanged()
                refreshEvents()
            }

             */
        }

        return view
    }

    override fun onEventAdded() {
        refreshEvents()
    }

    override fun onEventUpdated() {
        refreshEvents()
    }
}