package com.dicoding.todoapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID
import com.google.android.material.textfield.TextInputEditText

class DetailTaskActivity : AppCompatActivity() {

private lateinit var detailTaskViewModel: DetailTaskViewModel
private lateinit var viewModel: DetailTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val id = intent.getIntExtra(TASK_ID,0)
        val factory = ViewModelFactory.getInstance(this)
        detailTaskViewModel = ViewModelProvider(this, factory).get(DetailTaskViewModel::class.java)
        detailTaskViewModel.setTaskId(id)

        //TODO 11 : Show detail task and implement delete action

        val buttonDelete: Button = findViewById(R.id.btn_delete_task)
        buttonDelete.setOnClickListener {
            detailTaskViewModel.deleteTask()
        }

        val detailEdTitle = findViewById<TextInputEditText>(R.id.detail_ed_title)
        val detailEdDesc = findViewById<TextInputEditText>(R.id.detail_ed_description)
        val detailEdDueDate = findViewById<TextInputEditText>(R.id.detail_ed_due_date)

        detailTaskViewModel.task.observe(this){ task ->
            if (task!=null) {
                val edDueDate = DateConverter.convertMillisToString(task.dueDateMillis)
                detailEdTitle.setText(task.title)
                detailEdDesc.setText(task.description)
                detailEdDueDate.setText(edDueDate)
            }else{
                finish()
            }
        }
    }
}