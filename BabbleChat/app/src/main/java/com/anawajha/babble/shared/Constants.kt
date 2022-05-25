package com.anawajha.babble.shared

import com.anawajha.babble.logic.model.User
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket

class Constants {
    companion object{
        var userId:String? = null
        var MESSAGE = "message"
        var GROUP_MESSAGES = "groupMessages"
        var GROUP_ID = "group_id"
        var USER = "user"
        var SOURCE_ID = "source_id"
        var DESTINATION_ID = "destination_id"
        var IMAGE_MESSAGE = "image_message"
        var SOURCE_NAME = "source_name"
        var addedUsers = mutableListOf<User>()
        var mSocket: Socket? = IO.socket("http://192.168.1.171:8080")
        var IP = "192.168.1.171"

    }
}