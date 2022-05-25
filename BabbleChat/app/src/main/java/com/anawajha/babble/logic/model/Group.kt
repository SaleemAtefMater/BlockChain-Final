package com.anawajha.babble.logic.model

import com.anawajha.babble.shared.Constants
import org.json.JSONObject

data class Group(var id:String, var groupName:String?,var user: User) {
    fun encode(): JSONObject {
        var group= JSONObject()
        group.put("id",this.id)
        group.put("groupName",this.groupName)
        group.put("user",this.user.encode())
        return group
    }// encode
    companion object{
        fun decode(obj: JSONObject):Group{
            return Group(obj.getString("id"),obj.getString("groupName"), User.decode(obj.getJSONObject("user")))
        }// decode
    }// companion object
}