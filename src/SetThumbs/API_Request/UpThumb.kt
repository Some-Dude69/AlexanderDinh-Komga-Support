package SetThumbs.API_Request

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger


class UpThumb(
        private val client: OkHttpClient,
) {
    fun uploadSeriesThumbnail(thumbnail: Image, sendUrl:URL){
        Logger.getLogger(OkHttpClient::class.java.name).level = Level.FINE
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", "thumbnail",
                        thumbnail.image.toRequestBody("image/jpeg".toMediaType()))
                .build()

        val request = Request.Builder()
                .url(sendUrl)
                .post(requestBody)
                .build()

        val response = client.newCall(request).execute()
        response.body?.close()
    }
}