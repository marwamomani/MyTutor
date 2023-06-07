package com.psut.mytutor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserProfileTopicsAdapter(private val topicsList: List<Topic>):
    RecyclerView.Adapter<UserProfileTopicsAdapter.TopicsViewHolder>(){

    override fun getItemCount(): Int {
        // Return the number of the items in the list
        return topicsList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicsViewHolder {
        // Inflates (Shows) the actual record (The design) that is used to show the data
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_topic, parent, false)
        return TopicsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicsViewHolder, position: Int) {
        val currentTopic = topicsList[position]
        holder.topicNameTv.text = currentTopic.name
    }

    class TopicsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var topicNameTv: TextView = itemView.findViewById(R.id.tvTopicName)
    }
}