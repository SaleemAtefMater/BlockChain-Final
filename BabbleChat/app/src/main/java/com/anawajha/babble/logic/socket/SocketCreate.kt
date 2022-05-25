package com.anawajha.babble.logic.socket

import android.app.Application
import com.anawajha.babble.shared.Constants
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket

class SocketCreate:Application() {
    private var mSocket :Socket? = IO.socket("http://${Constants.IP}:8080")

    fun getSocket():Socket?{
        return mSocket
    }
}