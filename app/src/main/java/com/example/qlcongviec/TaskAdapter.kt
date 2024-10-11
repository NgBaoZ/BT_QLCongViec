package com.example.qlcongviec

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class TaskAdapter(context: Context, private val tasks: List<Task>) : ArrayAdapter<Task>(context, 0, tasks) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val task = getItem(position)
        view.findViewById<TextView>(R.id.textViewTaskName).text = task?.name
        view.findViewById<TextView>(R.id.textViewTaskDescription).text = task?.description
        return view
    }
}
