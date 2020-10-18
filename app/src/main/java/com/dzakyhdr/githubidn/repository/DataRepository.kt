package com.dzakyhdr.githubidn.repository


import com.dzakyhdr.githubidn.data.RetrofitInstance


class DataRepository() {

    private var response = RetrofitInstance.api

    suspend fun findUserDetailByUsername(username: String) = response.findUserDetailByUsername(username)

}