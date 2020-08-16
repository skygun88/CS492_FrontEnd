package com.example.test3

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

data class UserData(val img : String, val idk : String)

interface retrofitInterface {
    // api 를 관리해 주는 인터페이스

    // 프로필 이미지 보내기
    @Multipart
    @POST("imgtest/{template}")
    fun post_Porfile_Request(
        @Path("template") userId: Int,
        @Part imageFile : MultipartBody.Part): Call<UserData>
}

interface imgurInterface {
    // api 를 관리해 주는 인터페이스

    // 프로필 이미지 보내기
    @Multipart
    @POST("imgur/{template}")
    fun post_Porfile_Request(
        @Path("template") userId: Int,
        @Part imageFile : MultipartBody.Part): Call<UserData>
}