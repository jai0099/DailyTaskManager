package com.example.dailytaskmanager

import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
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
        val card: MaterialCardView = view.findViewById(R.id.cardTask)
        val cbDone: CheckBox = view.findViewById(R.id.cbDone)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = list[position]

        holder.cbDone.setOnCheckedChangeListener(null)

        holder.cbDone.text = task.title
        holder.cbDone.isChecked = task.isCompleted

        holder.tvDate.text =
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date(task.dueDate))

        if (task.isCompleted) {
            holder.cbDone.paintFlags =
                holder.cbDone.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.cbDone.setTextColor(
                holder.itemView.context.getColor(R.color.ad_gray)
            )
            holder.tvDate.setTextColor(
                holder.itemView.context.getColor(R.color.ad_gray)
            )
            holder.card.alpha = 0.6f
        } else {
            holder.cbDone.paintFlags =
                holder.cbDone.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.cbDone.setTextColor(
                holder.itemView.context.getColor(R.color.ad_dark_gray)
            )
            holder.tvDate.setTextColor(
                holder.itemView.context.getColor(R.color.ad_gray)
            )
            holder.card.alpha = 1f
        }


        // Checkbox toggle
        holder.cbDone.setOnCheckedChangeListener { _, _ ->
            onChecked(task)
        }

        // LONG PRESS ANYWHERE ON CARD = DELETE
        holder.card.setOnLongClickListener {
            onLongPress(task)
            true
        }

        // Single tap = edit
        holder.card.setOnClickListener {
            onClick(task)
        }
    }
}
