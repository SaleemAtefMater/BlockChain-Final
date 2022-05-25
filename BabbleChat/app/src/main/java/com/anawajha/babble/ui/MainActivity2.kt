package com.anawajha.babble.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anawajha.babble.R
import com.anawajha.babble.logic.socket.SocketCreate
import com.github.nkzawa.socketio.client.Socket

class MainActivity2 : AppCompatActivity() {
    private var mSocket: Socket? = null
    lateinit var app: SocketCreate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        app = application as SocketCreate
        mSocket = app.getSocket()

        mSocket!!.connect()

        mSocket!!.emit("user-view")

    }
}