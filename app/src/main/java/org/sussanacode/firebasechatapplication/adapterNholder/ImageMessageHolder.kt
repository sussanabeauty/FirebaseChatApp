package org.sussanacode.firebasechatapplication.adapterNholder

import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.sussanacode.firebasechatapplication.R
import org.sussanacode.firebasechatapplication.databinding.HolderImageMessageBinding
import org.sussanacode.firebasechatapplication.entity.Message

class ImageMessageHolder (val binding: HolderImageMessageBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindImageMessage(imagemsg: Message){
        loadImageIntoView(binding.ivmessage, imagemsg.imageUrl!!)

        binding.tvmessenger.text = if (imagemsg.name == null) "unknown" else imagemsg.name
        if (imagemsg.photoUrl != null) {
            loadImageIntoView(binding.ivmessenger, imagemsg.photoUrl!!)
        } else {
            binding.ivmessenger.setImageResource(R.drawable.ic_person)
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
                    Log.w("Message Adapter", "Getting download url was not successful.", e)
                }
        } else {
            Glide.with(view.context).load(url).into(view)
        }
    }

}