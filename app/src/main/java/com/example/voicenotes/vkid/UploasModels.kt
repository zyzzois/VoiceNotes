package com.example.voicenotes.vkid

class VKSaveInfo(val fileName: String) {
    fun getAttachment() = "note${fileName}"
}

class VKServerUploadInfo2(val uploadUrl: String)

class VKFileUploadInfo2(val file: String)




