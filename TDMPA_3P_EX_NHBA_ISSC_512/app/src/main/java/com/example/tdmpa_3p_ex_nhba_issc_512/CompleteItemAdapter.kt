package com.example.tdmpa_3p_ex_nhba_issc_512

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CompletedItemAdapter(
    private val context: Context,
    private val layoutResId: Int,
    private var items: MutableList<CompletedItemModel>,
    private val databaseHelper: DatabaseHelper
) : ArrayAdapter<CompletedItemModel>(context, layoutResId, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(layoutResId, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val item = getItem(position) ?: return view

        holder.tvName.text = item.name
        holder.tvReminder.text = item.reminder
        holder.tvType.text = when (item.type) {
            "event" -> "Evento"
            "note" -> "Nota"
            else -> "Tarea"
        }

        // Manejo de eventos como clics aquí, si es necesario

        return view
    }

    private class ViewHolder(view: View) {
        val tvName: TextView = view.findViewById(R.id.tvCompleteName)
        val tvReminder: TextView = view.findViewById(R.id.tvCompleteDate)
        val tvType: TextView = view.findViewById(R.id.tvCompleteStatus)
    }
}