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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TaskAdapter
    private lateinit var db: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding FIRST
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

        // Add new task
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        loadTasks()
    }

    override fun onResume() {
        super.onResume()
        // Refresh list after add/edit
        loadTasks()
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            val list = db.taskDao().getAllTasks()

            adapter.submitList(list)
            binding.tvCount.text = "Total Tasks: ${list.size}"
            binding.tvEmpty.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    // Delete confirmation dialog
    private fun showDeleteDialog(task: TaskEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
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
