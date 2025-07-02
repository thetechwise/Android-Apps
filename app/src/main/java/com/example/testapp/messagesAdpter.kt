package com.example.testapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class messagesAdpter(
    private val context: Context,
    private val messagesAdapterArrayList: ArrayList<msgModelclass>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_SEND = 1
    private val ITEM_RECEIVE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SEND) {
            val view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false)
            SenderViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messagesAdapterArrayList[position]

        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Yes") { dialog, _ ->
                    // Add Firebase delete logic here if needed
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            false
        }

        if (holder is SenderViewHolder) {
            holder.msgText.text = message.message
            Picasso.get().load(ChatWindowActivity.senderImg).into(holder.profileImage)
        } else if (holder is ReceiverViewHolder) {
            holder.msgText.text = message.message
            Picasso.get().load(ChatWindowActivity.receiverImageStatic).into(holder.profileImage)
        }
    }

    override fun getItemCount(): Int = messagesAdapterArrayList.size

    override fun getItemViewType(position: Int): Int {
        val message = messagesAdapterArrayList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid == message.senderid) {
            ITEM_SEND
        } else {
            ITEM_RECEIVE
        }
    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: CircleImageView = itemView.findViewById(R.id.profilerggg)
        val msgText: TextView = itemView.findViewById(R.id.msgsendertyp)
    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: CircleImageView = itemView.findViewById(R.id.pro)
        val msgText: TextView = itemView.findViewById(R.id.recivertextset)
    }
}
