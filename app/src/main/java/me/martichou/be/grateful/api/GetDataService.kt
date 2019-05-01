package me.martichou.be.grateful.api

import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface GetDataService {

    @GET("/api/files/get")
    fun getPhoto(@Query("fileName") fileName: String) : Deferred<Response<String>>

    @Multipart
    @POST("/api/files/upload")
    fun sendPhoto(@Part filePart: MultipartBody.Part, @Part("upload") name: RequestBody, @Query("tokenID") tokenID: String): Deferred<Response<Void>>

}