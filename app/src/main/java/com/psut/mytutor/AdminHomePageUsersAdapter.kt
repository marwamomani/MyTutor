package com.psut.mytutor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminHomePageUsersAdapter(private val usersList: List<UserData>, private val onDeleteUserListener: DeleteUserButton):
RecyclerView.Adapter<AdminHomePageUsersAdapter.UsersViewHolder>(){

    override fun getItemCount(): Int {
        return usersList.count()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        // Inflates (Shows) the actual record (The design) that is used to show the data
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tutor_for_admin, parent, false)
        return AdminHomePageUsersAdapter.UsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val currentUser = usersList[position]
        holder.userNameTv.text = currentUser.name
        holder.userRateTv.text = currentUser.rateAverage
        holder.deleteTutorIv.setOnClickListener{
            onDeleteUserListener.onDeleteUserButtonClicked(currentUser.userUID)
        }
    }

    class UsersViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var userNameTv: TextView = itemView.findViewById(R.id.tvTutorName)
        var userRateTv: TextView = itemView.findViewById(R.id.tvTutorRate)
        var deleteTutorIv: ImageView = itemView.findViewById(R.id.ivDeleteTutor)
    }
}

interface DeleteUserButton{
    fun onDeleteUserButtonClicked(userUID: String)
}