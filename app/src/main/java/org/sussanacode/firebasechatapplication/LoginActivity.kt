package org.sussanacode.firebasechatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.sussanacode.firebasechatapplication.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding

    lateinit var auth: FirebaseAuth


    val login: ActivityResultLauncher<Intent> =
        registerForActivityResult(FirebaseAuthUIActivityResultContract(), this::onLoginResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

      //  startUp()

    }


    public override fun onStart() {
        super.onStart()

        if (Firebase.auth.currentUser == null) {
            val loginIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.logo)
                .setAvailableProviders(listOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                )).build()

            login.launch(loginIntent)
        } else {
            startActivity(Intent(baseContext, MainActivity::class.java))
            finish()
        }
    }


    private fun onLoginResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            Log.d("User Authenticated", "Log in successful!")
            startActivity(Intent(baseContext, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(baseContext, "Failed to authenticate User", Toast.LENGTH_LONG).show()


            val response = result.idpResponse
            if (response == null) {
                Log.d("Action Canceled", "Sign in canceled")
            } else {
                Log.d("Action Failed", "Sign in error", response.error)
            }
        }
    }

}