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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDatabase.getDatabase(this)

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
            db.taskDao().insertTask(
                TaskEntity(
                    title = title,
                    dueDate = selectedDate
                )
            )
            finish()
        }
    }
}
