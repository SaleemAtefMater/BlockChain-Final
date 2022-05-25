package com.anawajha.babble.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.anawajha.babble.databinding.ActivityLoginBinding
import com.anawajha.babble.logic.firebase.LoginService
import com.anawajha.babble.logic.model.User
import com.anawajha.babble.logic.socket.SocketCreate
import com.anawajha.babble.shared.Constants
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val user = Firebase.auth.currentUser
    lateinit var app: SocketCreate
    private var mSocket: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as SocketCreate
        mSocket = app.getSocket()

        mSocket!!.connect()

        isLogin()

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

        user?.let {
            var userss =
                User(
                    user!!.uid,
                    user.displayName!!,
                    user.email!!,
                    user.photoUrl.toString(),
                    status = true,
                    typing = false
                )
            mSocket!!.emit("user-join", userss.encode())
        }
    }// onCreate

    private fun login() {
        val email = binding.edEmail.text.toString()
        val password = binding.edPassword.text.toString()

        if (email.isNotEmpty() && email.contains(".") && email.contains("@")) {
            if (password.isNotEmpty()) {
                if (LoginService.login(binding.root, this, email, password)) {
                    finish()
                }
            } else {
                binding.tfPassword.error = "Password should not be empty"
            }

        } else {
            binding.tfEmail.error = "Invalid email"
        }
    }// login

    private fun validation() {
        binding.edEmail.doOnTextChanged { text, start, before, count ->
            if (text!!.contains(".") && text.contains("@")) {
                binding.tfEmail.error = ""
            } else {
                binding.tfEmail.error = "Invalid email"
            }
        }// doOnTextChanged

        binding.edPassword.doOnTextChanged { text, start, before, count ->
            if (count > 8) {
                binding.tfPassword.error = ""
            } else {
                binding.tfPassword.error = "Password should be more than 8 character"
            }
        }// doOnTextChanged
    }// validation

    private fun isLogin() {
        val auth = Firebase.auth
        val sharedPrf = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        if (sharedPrf.getBoolean("isLogin", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }// isLogin

}// Login