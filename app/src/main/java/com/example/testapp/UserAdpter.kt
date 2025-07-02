package com.example.testapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private val context: Context,
    private val usersArrayList: ArrayList<Users>
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = usersArrayList[position]
        holder.username.text = user.userName
        holder.userstatus.text = user.status

        Picasso.get()
            .load(user.profilepic)
            .placeholder(R.drawable.photocamera)
            .into(holder.userimg)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatWindowActivity::class.java)
            intent.putExtra("nameeee", user.userName)
            intent.putExtra("reciverImg", user.profilepic)
            intent.putExtra("uid", user.userId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = usersArrayList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimg: CircleImageView = itemView.findViewById(R.id.userimg)
        val username: TextView = itemView.findViewById(R.id.username)
        val userstatus: TextView = itemView.findViewById(R.id.userstatus)
    }
}
