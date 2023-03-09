package com.example.voicenotes.vk

import android.net.Uri
import com.vk.api.sdk.VKApiJSONResponseParser
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKHttpPostCall
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class VKWallPostCommand(
    private val message: String? = null,
    private val file: Uri,
    private val fileName: String,
    private val ownerId: Int = 0,
): ApiCommand<VKServerUploadInfo2>() {

    override fun onExecute(manager: VKApiManager): VKServerUploadInfo2 {
        val callBuilder = VKMethodCall.Builder()
            .method("docs.save")
            .version(manager.config.version)
        message?.let {
            callBuilder.args("message", it)
        }

        if (ownerId != 0)
            callBuilder.args("owner_id", ownerId)

        val uploadInfo = getServerUploadInfo(manager)
        val attachments = uploadFile(
            uri = file,
            fileName = fileName,
            serverUploadInfo = uploadInfo,
            manager
        )
        callBuilder.args("attachments", attachments)
        return manager.execute(callBuilder.build(), CustomParser())
    }

    private fun getServerUploadInfo(manager: VKApiManager): VKServerUploadInfo2 {
        val uploadInfoCall = VKMethodCall.Builder()
            .method("docs.getUploadServer")
            .version(manager.config.version)
            .build()
        return manager.execute(uploadInfoCall, CustomParser())
    }

    private fun uploadFile(uri: Uri, fileName: String, serverUploadInfo: VKServerUploadInfo2, manager: VKApiManager): String {
        val fileUploadCall = VKHttpPostCall.Builder()
            .url(serverUploadInfo.uploadUrl)
            .args("file", uri, fileName)
            .timeout(TimeUnit.MINUTES.toMillis(5))
            .retryCount(3)
            .build()
        val fileUploadInfo = manager.execute(fileUploadCall, null, FileUploadInfoParser())
        val saveCall = VKMethodCall.Builder()
            .method("docs.save")
            .args("file", fileUploadInfo.file)
            .version(manager.config.version)
            .build()
        val saveInfo = manager.execute(saveCall, SaveInfoParser())
        return saveInfo.getAttachment()
    }

    private class FileUploadInfoParser: VKApiJSONResponseParser<VKFileUploadInfo2> {
        override fun parse(responseJson: JSONObject): VKFileUploadInfo2 {
            try {
                return VKFileUploadInfo2(file = responseJson.getString("file"))
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }

    private class SaveInfoParser: VKApiJSONResponseParser<VKSaveInfo> {
        override fun parse(responseJson: JSONObject): VKSaveInfo {
            try {
                val joResponse = responseJson.getJSONArray("response").getJSONObject(0)
                return VKSaveInfo(fileName = joResponse.toString())
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }

    private class CustomParser : VKApiJSONResponseParser<VKServerUploadInfo2> {
        override fun parse(responseJson: JSONObject): VKServerUploadInfo2 {
            try {
                val joResponse = responseJson.getJSONObject("response")
                return VKServerUploadInfo2(
                    uploadUrl = joResponse.getString("upload_url")
                )
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }

}
