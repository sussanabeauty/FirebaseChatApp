package org.sussanacode.firebasechatapplication.adapterNholder

import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.sussanacode.firebasechatapplication.R
import org.sussanacode.firebasechatapplication.databinding.HolderMessageBinding
import org.sussanacode.firebasechatapplication.entity.Message

class MessageHolder(val binding: HolderMessageBinding) : RecyclerView.ViewHolder(binding.root) {

    val currentUserName: String? = null

    fun bind(message: Message){
        binding.tvmessage.text = message.text
        setTextColor(message.name, binding.tvmessage)

        binding.tvmessenger.text =
            if(message.name == null) "unknown"
            else message.name

        if(message.photoUrl != null){
            loadImageIntoView(binding.ivmessenger, message.photoUrl!!)
        } else {
            binding.ivmessenger.setImageResource(R.drawable.ic_person)
        }

    }

    private fun setTextColor(userName: String?, textView: TextView) {
        if (userName != "anonymous" && currentUserName == userName && userName != null) {
            textView.setBackgroundResource(R.drawable.custom_incoming_message_textview)
            textView.setTextColor(Color.WHITE)
        } else {
            textView.setBackgroundResource(R.drawable.custom_message_textview)
            textView.setTextColor(Color.BLACK)
        }
    }

    private fun loadImageIntoView(view: ImageView, url: String) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Glide.with(view.context)
                        .load(downloadUrl)
                        .into(view)
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Getting download url was not successful.", e)
                }
        } else {
            Glide.with(view.context).load(url).into(view)
        }
    }


}