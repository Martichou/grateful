package me.martichou.be.grateful.persistance.remote

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GetDataService {

    @GET("/api/files/get")
    fun getPhoto(@Query("fileName") fileName: String) : Deferred<Response<String>>

}