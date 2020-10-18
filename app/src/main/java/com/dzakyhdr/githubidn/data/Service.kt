package com.dzakyhdr.githubidn.data

import com.dzakyhdr.githubidn.model.GithubResponse
import com.dzakyhdr.githubidn.util.Constans.Companion.API_KEY
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {


    @GET("/search/users")
    @Headers("Authorization: token $API_KEY")
    suspend fun findUserDetailByUsername(
        @Query("q")
        searchView: String
    ): Response<GithubResponse>
}