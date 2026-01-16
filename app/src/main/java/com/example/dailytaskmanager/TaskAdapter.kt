package com.example.dailytaskmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onChecked: (TaskEntity) -> Unit,
    private val onLongPress: (TaskEntity) -> Unit,
    private val onClick: (TaskEntity) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var list = listOf<TaskEntity>()

    fun submitList(tasks: List<TaskEntity>) {
        list = tasks
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbDone: CheckBox = view.findViewById(R.id.cbDone)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = list[position]

        holder.cbDone.setOnCheckedChangeListener(null)

        holder.cbDone.text = task.title
        holder.cbDone.isChecked = task.isCompleted

        holder.tvDate.text =
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date(task.dueDate))

        // Checkbox
        holder.cbDone.setOnCheckedChangeListener { _, _ ->
            onChecked(task)
        }

        // Long press delete
        holder.itemView.setOnLongClickListener {
            onLongPress(task)
            true
        }

        // Single tap edit
        holder.itemView.setOnClickListener {
            onClick(task)
        }
    }
}
