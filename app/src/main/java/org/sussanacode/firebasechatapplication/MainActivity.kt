package org.sussanacode.firebasechatapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import org.sussanacode.firebasechatapplication.adapterNholder.MessagesAdapter
import org.sussanacode.firebasechatapplication.databinding.ActivityMainBinding
import org.sussanacode.firebasechatapplication.entity.Message
import org.sussanacode.firebasechatapplication.others.MyButtonObserver
import org.sussanacode.firebasechatapplication.others.MyOpenDocumentContract
import org.sussanacode.firebasechatapplication.others.MyScrollToBottomObserver


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding

    lateinit var auth: FirebaseAuth
    lateinit var firebaseDB : FirebaseDatabase
    lateinit var messageAdapter: MessagesAdapter


    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        onImageSelected(uri)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = Firebase.auth
        //congifFirebase()


        firebaseInit()
        setUpEvents()

    }


    private fun onImageSelected(uri: Uri) {
        Log.d("imageUrl", "Uri: $uri")
        val user = auth.currentUser
        val tempMsg = Message(null, getUserName(), getUserProfile(), LOADING_IMAGE_URL)
        firebaseDB.reference
            .child("message")
            .push()
            .setValue(tempMsg, DatabaseReference.CompletionListener { databaseError, databaseReference ->
                    if (databaseError != null) {
                        Log.d("Failed to Write to DB", "Failed to write message to FirebaseDB.", databaseError.toException())
                        return@CompletionListener
                    }

                    // Build a StorageReference and then upload the file
                    val key = databaseReference.key
                    val storageReference = Firebase.storage
                        .getReference(user!!.uid)
                        .child(key!!)
                        .child(uri.lastPathSegment!!)
                    saveImageInStorage(storageReference, uri, key)
                })
    }

    private fun saveImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {

        // First upload the image to Cloud Storage
        storageReference.putFile(uri)
            .addOnSuccessListener(this) { taskSnapshot ->
                // After the image loads, get a public downloadUrl for the image and add it to the message.
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val message = Message(null, getUserName(), getUserProfile(), uri.toString())
                        firebaseDB.reference
                            .child("messages")
                            .child(key!!)
                            .setValue(message)
                    }
            }
            .addOnFailureListener(this) { e ->

                Log.d("Image Loaded", "Image upload task was unsuccessful.", e)
            }

    }


    private fun setUpEvents() {
        firebaseDB = Firebase.database

        val msgReference = firebaseDB.reference.child("messages")
        val msgOptions = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(msgReference, Message::class.java).build()

        //leod message to recycler view
        messageAdapter = MessagesAdapter(msgOptions, getUserName())

        binding.progressBar.visibility = ProgressBar.INVISIBLE
        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.rvmessages.layoutManager = manager
        binding.rvmessages.adapter = messageAdapter

        messageAdapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(binding.rvmessages, messageAdapter, manager)
        )

        binding.etmessage.addTextChangedListener(MyButtonObserver(binding.ivSendButton))


        //send new message
        binding.ivSendButton.setOnClickListener {
            val message = Message(binding.etmessage.text.toString(), getUserName(), getUserProfile(), null)
            firebaseDB.reference.child("messages").push().setValue(message)
            binding.etmessage.setText("")

            binding.ivAddMessage.setOnClickListener {
                openDocument.launch(arrayOf("image/*"))
            }
        }

    }

//    private fun congifFirebase() {
//        if(BuildConfig.DEBUG){
//            Firebase.auth.useEmulator("10.0.2.2", 9099)
//            Firebase.database.useEmulator("10.0.2.2", 9000)
//           // Firebase.storage.useEmulator("10.0.2.2", 9199)
//
//        }
//    }


    private fun firebaseInit() {
        if (auth.currentUser == null) {
            startActivity(Intent(baseContext, LoginActivity::class.java))
            finish()
            return
        }

    }

    override fun onStart() {
        super.onStart()
        //is user already sign in
        if (auth.currentUser == null) {
            startActivity(Intent(baseContext, LoginActivity::class.java))
            finish()
            return
        }
    }

    public override fun onPause() {
        messageAdapter.stopListening()
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        messageAdapter.startListening()
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(baseContext)
        startActivity(Intent(baseContext, LoginActivity::class.java))
        finish()
    }


    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else "Unknown"
    }

    private fun getUserProfile(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }




    companion object {
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }
}