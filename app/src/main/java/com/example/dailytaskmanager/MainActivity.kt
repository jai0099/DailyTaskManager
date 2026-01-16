package com.example.dailytaskmanager

import android.os.Bundle
import android.view.View
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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDatabase.getDatabase(this)

        adapter = TaskAdapter { task ->
            lifecycleScope.launch {
                db.taskDao().updateTask(
                    task.copy(isCompleted = !task.isCompleted)
                )
                loadTasks()
            }
        }

        binding.recyclerTasks.layoutManager = LinearLayoutManager(this)
        binding.recyclerTasks.adapter = adapter

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
}
