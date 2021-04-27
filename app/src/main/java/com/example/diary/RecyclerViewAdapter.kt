package com.example.diary

import android.util.Log
import com.example.diary.data.Task
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.sql.Date
import java.sql.Timestamp

const val TASK_COUNT = 2

class RecyclerViewAdapter(private val onClick: (Task) -> Unit) :
    ListAdapter<Task, RecyclerViewAdapter.ViewHolder>(TaskDiffCallback) {

    class ViewHolder(view: View, private val onClick: (Task) -> Unit) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private var timeTextView: TextView? = null
        private var nameTaskTextView: TextView? = null
        private var currentTask: Task? = null

        init {
            timeTextView = view.findViewById(R.id.timeTextView)
            nameTaskTextView = view.findViewById(R.id.nameTaskTextView)

            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            currentTask?.let {
                onClick(it)
            }
        }

        fun bind(task: Task) {
            currentTask = task

            timeTextView?.text = getFullTime(task.dateStart, task.dateFinish)
            nameTaskTextView?.text = task.name

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_view, viewGroup, false)

        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))

    }


    override fun getItemCount() = getCurrentList().size

}

object TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }
}