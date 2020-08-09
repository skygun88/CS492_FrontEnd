package com.example.test3

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

data class UserData(val img : String, val idk : String)

interface retrofitInterface {
    // api 를 관리해 주는 인터페이스

    // 프로필 이미지 보내기
    @Multipart
    @POST("imgtest/")
    fun post_Porfile_Request(
        @Part("userId") userId: String,
        @Part imageFile : MultipartBody.Part): Call<UserData>


}