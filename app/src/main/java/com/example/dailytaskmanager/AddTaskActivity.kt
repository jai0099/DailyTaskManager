package com.example.dailytaskmanager

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dailytaskmanager.databinding.ActivityAddTaskBinding
import kotlinx.coroutines.launch
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var db: TaskDatabase

    private var selectedDate: Long = 0L
    private var taskId: Int = -1   // for Edit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDatabase.getDatabase(this)

        // Check if Edit mode
        taskId = intent.getIntExtra("task_id", -1)
        if (taskId != -1) {
            binding.etTitle.setText(intent.getStringExtra("task_title"))
            selectedDate = intent.getLongExtra("task_date", 0L)
            binding.tvDate.text = Date(selectedDate).toString()
            binding.btnSave.text = "Update Task"
        }

        binding.btnDate.setOnClickListener {
            openDatePicker()
        }

        binding.btnSave.setOnClickListener {
            saveTask()
        }
    }

    private fun openDatePicker() {
        val cal = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, y, m, d ->
                cal.set(y, m, d)
                selectedDate = cal.timeInMillis
                binding.tvDate.text = Date(selectedDate).toString()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveTask() {
        val title = binding.etTitle.text.toString().trim()

        if (title.isEmpty()) {
            binding.etTitle.error = "Title required"
            return
        }

        if (selectedDate == 0L) {
            Toast.makeText(this, "Select due date", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (taskId == -1) {
                // ADD TASK
                db.taskDao().insertTask(
                    TaskEntity(
                        title = title,
                        dueDate = selectedDate
                    )
                )
            } else {
                // UPDATE TASK
                db.taskDao().updateTask(
                    TaskEntity(
                        id = taskId,
                        title = title,
                        dueDate = selectedDate,
                        isCompleted = false
                    )
                )
            }
            finish()
        }
    }
}
