package com.example.voicenotes.presentation.screens

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import com.example.voicenotes.R
import com.example.voicenotes.app.NoteListApp
import com.example.voicenotes.databinding.ActivityAuthorizationBinding
import com.example.voicenotes.utils.showToast
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope

class AuthorizationActivity : AppCompatActivity() {

    private val component by lazy {
        (application as NoteListApp).component
    }

    private val connectingText by lazy { resources.getString(R.string.connecting_msg) }
    private val authText by lazy { resources.getString(R.string.authorization_using) }

    private var isSuccess = false
    private var inProcess = false

    private val isAlreadyAuthorized by lazy {
        getSharedPreferences(AUTH_TABLE_NAME, Context.MODE_PRIVATE)
    }
    private val authStateEditor by lazy {
        isAlreadyAuthorized.edit()
    }

    private val authLauncher = VK.login(this as ComponentActivity) { result : VKAuthenticationResult ->
        when (result) {
            is VKAuthenticationResult.Success -> {
                authStateEditor.putBoolean(AUTH_STATE, true)
                authStateEditor.apply()
                isSuccess = true
                inProcess = false
                showToast(RES_SIGN_IN_VK_SUCCESS)
                val intent = NotesActivity.newIntentOpenNotesActivity(this)
                startActivity(intent)
            }
            is VKAuthenticationResult.Failed -> {
                isSuccess = false
                inProcess = false
                showToast(RES_SIGN_IN_VK_FAILED)
            }
        }
    }

    private val binding by lazy { ActivityAuthorizationBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkIntent()
        checkAuthState()
        setupButtonsAction()
    }

    private fun checkIntent() {
        val ableToEditAuth = intent.getBooleanExtra(OPEN_FOR_EDIT, false)
        if (ableToEditAuth) {
            authStateEditor.putBoolean(AUTH_STATE, false)
            authStateEditor.apply()
        }
    }

    private fun checkAuthState() {
         if(isAlreadyAuthorized.getBoolean(AUTH_STATE, false)) {
             showToast("Вы уже успешно авторизованы")
             val intent = NotesActivity.newIntentOpenNotesActivity(this)
             startActivity(intent)
         } else showToast("Вы не авторизованы")
    }

    private fun setupButtonsAction() = with(binding) {
        buttonSignInVK.setOnClickListener {
            inProcess = true
            textView.text = connectingText
            buttonSignInVK.visibility = View.GONE
            //buttonSignUpFirebase.visibility = View.GONE
            //buttonSignInFirebase.visibility = View.GONE
            buttonSkipAuth.visibility = View.GONE
            authLauncher.launch(arrayListOf(VKScope.DOCS))
        }
        buttonSkipAuth.setOnClickListener {
            val intent = NotesActivity.newIntentOpenNotesActivity(this@AuthorizationActivity)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!inProcess) {
            with(binding) {
                textView.text = authText
                buttonSignInVK.visibility = View.VISIBLE
                //buttonSignUpFirebase.visibility = View.VISIBLE
                //buttonSignInFirebase.visibility = View.VISIBLE
                buttonSkipAuth.visibility = View.VISIBLE
            }
        }
    }

    companion object {

        private const val TAG = "AUTHORIZATION"
        private const val AUTH_TABLE_NAME = "Auth"
        private const val AUTH_STATE = "auth state"
        private const val REC_SIGN_IN_FAILED = "failed to sign in"
        private const val RES_SIGN_IN_SUCCESS = "successfully signed up"
        private const val RES_SIGN_IN_VK_SUCCESS = "Вы успешно авторизованы через VK"
        private const val RES_SIGN_IN_VK_FAILED = "failed to sign in VK"
        private const val OPEN_FOR_EDIT = "is open for edit"

        fun newIntentOpenNotesActivity(context: Context, isOpenForEdit: Boolean): Intent {
            val intent = Intent(context, AuthorizationActivity::class.java)
            intent.putExtra(OPEN_FOR_EDIT, isOpenForEdit)
            return intent
        }
    }
}