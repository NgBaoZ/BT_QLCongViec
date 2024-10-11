package com.example.qlcongviec

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: TaskDatabaseHelper
    private lateinit var listView: ListView
    private lateinit var taskAdapter: ArrayAdapter<String>
    private lateinit var tasks: MutableList<Task> // Danh sách chứa các task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo SQLite helper và các thành phần giao diện
        dbHelper = TaskDatabaseHelper(this)
        listView = findViewById(R.id.listView)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)

        // Tải dữ liệu từ SQLite và hiển thị trong ListView
        loadTasks()

        // Sự kiện khi nhấp vào một item trong ListView
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedTask = tasks[position]
            // Mở dialog hoặc activity để chỉnh sửa hoặc xóa task
            showEditDeleteDialog(selectedTask)
        }

        // Sự kiện khi nhấn nút "Thêm Công Việc"
        buttonAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    // Hàm tải tất cả các task từ cơ sở dữ liệu và hiển thị trên ListView
    private fun loadTasks() {
        tasks = dbHelper.getAllTasks().toMutableList()

        // Sử dụng TaskAdapter để hiển thị cả tên và mô tả công việc
        val taskAdapter = TaskAdapter(this, tasks)
        listView.adapter = taskAdapter
    }

    // Hiển thị dialog để thêm công việc mới
    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_task, null)
        builder.setView(dialogView)

        val editTextName = dialogView.findViewById<EditText>(R.id.editText)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextDescription)

        builder.setTitle("Thêm Công Việc")
        builder.setPositiveButton("Thêm") { dialog, _ ->
            val name = editTextName.text.toString()
            val description = editTextDescription.text.toString()

            if (name.isNotEmpty()) {
                val newTask = Task(0, name, description)
                dbHelper.addTask(newTask)
                loadTasks() // Cập nhật lại danh sách sau khi thêm task mới
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    // Hiển thị dialog để chỉnh sửa hoặc xóa task
    private fun showEditDeleteDialog(task: Task) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(task.name)
        builder.setItems(arrayOf("Chỉnh Sửa", "Xóa")) { dialog, which ->
            when (which) {
                0 -> showEditTaskDialog(task) // Chỉnh sửa
                1 -> {
                    dbHelper.deleteTask(task.id) // Xóa task
                    loadTasks() // Cập nhật lại danh sách
                }
            }
            dialog.dismiss()
        }
        builder.create().show()
    }

    // Hiển thị dialog để chỉnh sửa công việc
    private fun showEditTaskDialog(task: Task) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_task, null)
        builder.setView(dialogView)

        val editTextName = dialogView.findViewById<EditText>(R.id.editText)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextDescription)

        editTextName.setText(task.name)
        editTextDescription.setText(task.description)

        builder.setTitle("Chỉnh Sửa Công Việc")
        builder.setPositiveButton("Cập Nhật") { dialog, _ ->
            val updatedTask = Task(task.id, editTextName.text.toString(), editTextDescription.text.toString())
            dbHelper.updateTask(updatedTask)
            loadTasks() // Cập nhật lại danh sách sau khi chỉnh sửa
            dialog.dismiss()
        }
        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

}

