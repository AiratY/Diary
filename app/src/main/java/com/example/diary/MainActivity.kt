package com.example.diary


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diary.data.Task
import com.example.diary.taskDescr.taskDescriptionActivity
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import kotlin.random.Random

const val TASK_ID = "task id"
const val TASK_START_TIME = "task start time"
const val TASK_END_TIME = "task end time"
const val YEAR = "year"
const val MONTH = "month"
const val DAY = "day"
const val HOUR = "hour"
const val MINUTES = "minutes"


class MainActivity : AppCompatActivity() {

    private val newTaskActivityRequestCode = 1
    /*
    private val tasksListViewModel by viewModels<TasksListViewModel> {
        TasksListViewModelFactory(this)
    }

     */
    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = realmInit(this)

        val loadFromDBTaskList: List<Task> = realm.where<Task>().findAll()

        val taskRecyclerView:RecyclerView = findViewById(R.id.taskRecyclerView)
        var taskAdapter = RecyclerViewAdapter{task -> taskItemOnClick(task)}
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = taskAdapter

        val dateTimeNow = LocalDateTime.now()

        val calendarView: CalendarView = findViewById(R.id.calendarView)

        getListTaskToday(taskAdapter,loadFromDBTaskList, dateTimeNow.year, dateTimeNow.monthValue-1, dateTimeNow.dayOfMonth)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            getListTaskToday(taskAdapter,loadFromDBTaskList, year, month, dayOfMonth)
        }
    }
    fun getListTaskToday(taskAdapter:RecyclerViewAdapter,initialTaskList: List<Task>, year: Int, month: Int, dayOfMonth: Int) {

        for(i in initialTaskList.indices) {
            Log.d("ItemFromListDB", initialTaskList[i].name+" "+ initialTaskList[i].id)
        }

        val listTask: List<Task> = initialTaskList
        var listTaskToday: MutableList<Task> = mutableListOf<Task>()
        val currentDate = Date(year-1900,month,dayOfMonth)

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

        for(i in listTask.indices) {
            Log.e("Date", simpleDateFormat.format(listTask[i].dateStart)+" "+currentDate.toString())
            if(simpleDateFormat.format(listTask[i].dateStart).toString() == currentDate.toString()){
                listTaskToday.add(listTask[i])

            }
        }

        var listTaskEveryDay: MutableList<Task> = mutableListOf<Task>()
        for(i in 0..23) {
            var task = Task()
            task.id = Random.nextInt()
            task.dateStart = java.util.Date(currentDate.year,currentDate.month, currentDate.date, i, 0,0)
            task.dateFinish = java.util.Date(currentDate.year,currentDate.month, currentDate.day, i+1, 0,0)
            listTaskEveryDay.add(task)
        }

        listTaskToday!!.sortBy{it.dateStart}
        for(i in 0..23) {
            for(j in listTaskToday.indices) {
                if(listTaskEveryDay[i].dateStart == listTaskToday[j].dateStart){

                    listTaskEveryDay[i] = listTaskToday[j]
                }
            }
        }

        taskAdapter.submitList(listTaskEveryDay as MutableList<Task>)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun generateFakeValues(): List<String> {
        val values = mutableListOf<String>()
        for(i in 0..23) {
            values.add("$i:00-"+(i+1)+":00")
        }
        return values
    }

    public fun taskItemOnClick(task: Task) {
        val intent = Intent(this, taskDescriptionActivity::class.java)

        intent.putExtra(TASK_ID, task.id)
        putExtraDateTimeTask(TASK_START_TIME, intent, task.dateStart)
        putExtraDateTimeTask(TASK_END_TIME, intent, task.dateFinish)

        startActivity(intent)
    }
    fun putExtraDateTimeTask(strNameQuery: String, intent: Intent, date:java.util.Date) {
         intent.putExtra(strNameQuery+ YEAR, date.year)
         intent.putExtra(strNameQuery+ MONTH, date.month)
         intent.putExtra(strNameQuery+ DAY, date.date)
         intent.putExtra(strNameQuery+ HOUR, date.hours)
         intent.putExtra(strNameQuery+ MINUTES, date.minutes)
     }
}



