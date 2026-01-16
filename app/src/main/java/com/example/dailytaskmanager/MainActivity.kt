package com.example.dailytaskmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailytaskmanager.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TaskAdapter
    private lateinit var db: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDatabase.getDatabase(this)

        adapter = TaskAdapter(
            // Checkbox complete / incomplete
            onChecked = { task ->
                lifecycleScope.launch {
                    db.taskDao().updateTask(
                        task.copy(isCompleted = !task.isCompleted)
                    )
                    loadTasks()
                }
            },

            // Long press -> delete
            onLongPress = { task ->
                showDeleteDialog(task)
            },

            // Single tap -> edit
            onClick = { task ->
                val intent = Intent(this, AddTaskActivity::class.java)
                intent.putExtra("task_id", task.id)
                intent.putExtra("task_title", task.title)
                intent.putExtra("task_date", task.dueDate)
                startActivity(intent)
            }
        )

        binding.recyclerTasks.layoutManager = LinearLayoutManager(this)
        binding.recyclerTasks.adapter = adapter

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        loadTasks()
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    // TODAY FILTER LOGIC
    private fun loadTasks() {
        lifecycleScope.launch {

            val cal = Calendar.getInstance()

            // Start of today
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val startOfDay = cal.timeInMillis

            // End of today
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 999)
            val endOfDay = cal.timeInMillis

            val list = db.taskDao().getTodayTasks(startOfDay, endOfDay)

            adapter.submitList(list)
            binding.tvCount.text = "Today Tasks: ${list.size}"
            binding.tvEmpty.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    // Delete confirmation dialog
    private fun showDeleteDialog(task: TaskEntity) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_task))
            .setMessage(getString(R.string.delete_confirm))
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    db.taskDao().deleteTask(task)
                    loadTasks()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
