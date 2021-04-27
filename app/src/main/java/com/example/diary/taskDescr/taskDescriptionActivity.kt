package com.example.diary.taskDescr

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color.red
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import android.widget.ViewAnimator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.example.diary.*
import com.example.diary.data.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random

const val DATE_FORMAT = "yyyy-MM-dd"
const val TASK_NAME = "name"
const val TASK_DESCRIPTION = "description"


class taskDescriptionActivity : AppCompatActivity() {
    /*
    private val taskDescrViewModel by viewModels<TaskDescrViewModel> {
        TaskDescrViewModelFactory(this)
    }
    */


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_description)

        var realm:Realm = realmInit(this)

        val initialTaskList: List<Task> = realm.where<Task>().findAll()
        var taskLiveData: MutableLiveData<List<Task>> = MutableLiveData(initialTaskList)

        var modeAction:Int = 0
        var isEdit:Boolean = false
        var currentTaskId: Int? = null
        var currentTaskDateStart: Date = Date(1,1,1, 1,1,1)
        var currentTaskDateEnd: Date = Date(1,1,1, 1,1,1)

        var dateTimeTaskTextView: TextView = findViewById(R.id.dateTimeTaskTextView)
        var nameTaskTextView: TextView = findViewById(R.id.nameTaskTextView)
        var descriptionTaskTextView: TextView = findViewById(R.id.descriptionTaskTextView)

        var nameTaskInputLayout:TextInputLayout = findViewById(R.id.nameTaskInputLayout)
        var descriptionTaskInputLayout:TextInputLayout = findViewById(R.id.descriptionTaskInputLayout)
        var nameTaskEditText:TextInputEditText = findViewById(R.id.nameTaskEditText)
        var descriptionTaskEditText:TextInputEditText = findViewById(R.id.descriptionTaskEditText)
        var actionTaskButton:Button = findViewById(R.id.actionTaskButton)
        var removeTaskButton:Button = findViewById(R.id.removeTaskButton)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentTaskId = bundle.getInt(TASK_ID)
            currentTaskDateStart = getDateFromIntent(bundle, TASK_START_TIME)
            currentTaskDateEnd = getDateFromIntent(bundle, TASK_END_TIME)
            Log.e("ErrorAdd", getDateFromIntent(bundle, TASK_END_TIME).toString())
        }

        fun viewVisibilityMode() {
            isEdit = false
            nameTaskTextView.visibility = VISIBLE
            descriptionTaskTextView.visibility = VISIBLE

            nameTaskInputLayout.visibility = GONE
            descriptionTaskInputLayout.visibility = GONE
            actionTaskButton.text = "Изменить"
            removeTaskButton.visibility = VISIBLE
        }

        fun editVisibilityMode() {
            isEdit = true
            nameTaskTextView.visibility = GONE
            descriptionTaskTextView.visibility = GONE

            nameTaskInputLayout.visibility = VISIBLE
            descriptionTaskInputLayout.visibility = VISIBLE
            actionTaskButton.text = "Сохранить"
            removeTaskButton.visibility = GONE
        }

        fun changeTaskVisibilityMode(task: Task) {
            editVisibilityMode()
            nameTaskEditText.setText(task.name)
            descriptionTaskEditText.setText(task.description)
            modeAction = 2

        }
        fun errorVisibilityMode() {
            nameTaskEditText.hint = "Обязательное поле для заполнения"
            nameTaskEditText.setHintTextColor(R.color.red)
            //nameTaskInputLayout.isErrorEnabled = true
            Log.e("ErrorAdd", "Поле name не должно быть пустым")
        }

        currentTaskId?.let {
            val currentTask = getTaskForId(taskLiveData, it)
            if(currentTask == null) {
                modeAction = 0
                editVisibilityMode()
                dateTimeTaskTextView.text = getFullDateTime(currentTaskDateStart, currentTaskDateEnd)
            }  else {
                modeAction = 1
                viewVisibilityMode()
                dateTimeTaskTextView.text = getFullDateTime(currentTask.dateStart, currentTask.dateFinish)
                nameTaskTextView.text = "Название:\n" + currentTask?.name
                descriptionTaskTextView.text = "Описание:\n"+currentTask?.description
            }
            actionTaskButton.setOnClickListener {
                if(modeAction == 0) {
                    Log.e("taskadd", "прошли режим 0")
                    if (nameTaskEditText.text.toString() != "") {
                        Log.e("taskadd", "прошли проверку на пустоту")
                        addTaskToDB(realm, nameTaskEditText.text.toString(), descriptionTaskEditText.text.toString(), currentTaskDateStart, currentTaskDateEnd)
                        goToHome()
                    } else {
                        errorVisibilityMode()
                    }
                } else if(modeAction == 1) {
                    changeTaskVisibilityMode(currentTask!!)
                } else if(modeAction == 2) {
                    if (nameTaskEditText.text.toString() != "") {
                        changeTaskFromDB(realm,nameTaskEditText.text.toString(),descriptionTaskEditText.text.toString(), currentTask!!.id)
                        goToHome()
                    } else {
                        errorVisibilityMode()
                    }
                } else {
                    Log.d("task","save")
                    //removeTask()
                }

            }
            removeTaskButton.setOnClickListener {
                deleteTaskFromDB(realm, currentTask!!.id)
                goToHome()
            }
        }


    }

    private fun deleteTaskFromDB(realm: Realm, id: Int) {
        realm.executeTransaction { transactionRealm: Realm ->
            val innerOtherTask : Task = transactionRealm.where<Task>().equalTo("id", id).findFirst()!!
            innerOtherTask.deleteFromRealm()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    fun getTaskForId(taskLiveData: MutableLiveData<List<Task>>,id: Int): Task? {
        taskLiveData.value?.let { tasks ->
            return tasks.firstOrNull{ it.id == id}
        }
        return null
    }


    fun addTaskToDB(realm:Realm, name: String, description: String, dateStart: Date, dateEnd: Date) {
        if (!name.isNullOrEmpty()) {

            realm.executeTransaction { transactionRealm: Realm ->

                var task = transactionRealm.createObject(Task::class.java)
                task.id = Random.nextInt()
                Log.e("taskadd", "прошли проверку на пустоту"  + realm.where<Task>().equalTo("id", task.id).count() +"  "+ task.id)
                /*
                while (realm.where<Task>().equalTo("name",task.id.toString()).count() > 0) {
                    Log.e("taskadd", "taskid"+ task.id+"")
                    task.id = Random.nextInt()
                }
                 */
                Log.e("taskadd", "taskid"+ task.id+"")

                task.dateStart = dateStart
                task.dateFinish = dateEnd
                task.name = name
                task.description = description
                Log.e("taskadd", dateStart.toString()+" "+dateEnd+" "+name+" "+description+" "+task.id)

            }
        }
    }
    fun getTaskForId(realm:Realm, id: Int):Task? {
        var task:Task = Task()
        realm.executeTransaction { transactionRealm: Realm ->
            task = transactionRealm.where<Task>().equalTo("id", id).findFirst()!!
        }
        return if(task.id == null) {
            null
        } else {
            task
        }
    }
    fun changeTaskFromDB(realm:Realm, name: String, description: String, id:Int) {
        if (!name.isNullOrEmpty()) {

            realm.executeTransaction { transactionRealm: Realm ->

                val innerOtherTask : Task = transactionRealm.where<Task>().equalTo("id", id).findFirst()!!
                innerOtherTask.name = name
                innerOtherTask.description = description
            }
        }
    }

    fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    fun getDateFromIntent(bundle: Bundle, strConst: String):Date {
        return Date(bundle.getInt(strConst + YEAR), bundle.getInt(strConst + MONTH),bundle.getInt(
                strConst + DAY),bundle.getInt(strConst + HOUR),bundle.getInt(
                strConst + MINUTES),0)
    }

    fun getFullDateTime(dateStart: Date,dateEnd:Date): String {
        val pattern = DATE_FORMAT
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date: String = simpleDateFormat.format(dateStart)
        return "Дата и время:\n" + date + " "+ getFullTime(dateStart, dateEnd)
    }
}