package org.sussanacode.firebasechatapplication.entity

data class Message(
    var text: String? = null,
    var name: String? = null,
    var photoUrl: String? = null,
    var imageUrl: String? = null
)
