package org.sussanacode.firebasechatapplication.adapterNholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import org.sussanacode.firebasechatapplication.databinding.HolderImageMessageBinding
import org.sussanacode.firebasechatapplication.databinding.HolderMessageBinding
import org.sussanacode.firebasechatapplication.entity.Message

class MessagesAdapter (val messageOption: FirebaseRecyclerOptions<Message>, val currentUserName: String?) : FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder>(messageOption) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

       return if(viewType == VIEW_TYPE_TEXT){
            val msgBinding = HolderMessageBinding.inflate(layoutInflater, parent, false)
             MessageHolder(msgBinding)
        }else {
            val imgBinding = HolderImageMessageBinding.inflate(layoutInflater, parent, false)
            ImageMessageHolder(imgBinding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, messageType: Message) {

        if(messageOption.snapshots[position].text != null){
            when(holder){
                is MessageHolder -> {
                    holder.bind(messageType)
                  //  (holder as MessageViewHolder).bind(messageOption)
                }

                is ImageMessageHolder -> {
                    holder.bindImageMessage(messageType)
                   // (holder as ImageMessageHolder).bindImageMessage(model)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageOption.snapshots[position].text != null) VIEW_TYPE_TEXT else VIEW_TYPE_IMAGE
    }


    companion object {
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
    }
}