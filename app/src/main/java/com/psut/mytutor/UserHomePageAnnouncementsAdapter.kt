package com.psut.mytutor

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserHomePageAnnouncementsAdapter(private val announcementsList: List<Announcement>, private val onTutorNameClicked: TutorNameClicked):
    RecyclerView.Adapter<UserHomePageAnnouncementsAdapter.AnnouncementsViewHolder>() {

    override fun getItemCount(): Int {
        // Return the number of the items in the list
        return announcementsList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementsViewHolder {
        // Inflates (Shows) the actual record (The design) that is used to show the data
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tutor_record, parent, false)
        return AnnouncementsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementsViewHolder, position: Int) {
        val currentAnnouncement = announcementsList[position]
        holder.topicNameTv.text = currentAnnouncement.topic.name
        holder.tutorNameTv.text = currentAnnouncement.userName
        holder.tutorNameTv.paintFlags = holder.tutorNameTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        holder.tutorNameTv.setOnClickListener{
            // Open tutor profile page
            onTutorNameClicked.openProfilePage(currentAnnouncement.userID)
        }
    }

    class AnnouncementsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var topicNameTv: TextView = itemView.findViewById(R.id.tvTopicName)
        var tutorNameTv: TextView = itemView.findViewById(R.id.tvTutorName)
    }
}

interface TutorNameClicked{
    fun openProfilePage(userUID: String)
}