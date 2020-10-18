package com.dzakyhdr.githubidn.ui.home

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dzakyhdr.githubidn.GithubApplication
import com.dzakyhdr.githubidn.model.GithubResponse
import com.dzakyhdr.githubidn.repository.DataRepository
import com.dzakyhdr.githubidn.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class HomeViewModel(
    app: Application,
    val dataRepository: DataRepository)
    : AndroidViewModel(app) {

    val users: MutableLiveData<Resource<GithubResponse>> = MutableLiveData()
    var usersResponse: GithubResponse? = null


    fun getUsers(userName: String) = viewModelScope.launch {
        safeGetUsers(userName)
    }

    private fun handleGetUsers(response: Response<GithubResponse>): Resource<GithubResponse>{
        if (response.isSuccessful){
            response.body()?.let {
                if (usersResponse == null){
                    usersResponse = it
                } else {
                    val oldUsers = usersResponse?.items
                    val newUsers = it.items
                    oldUsers?.addAll(newUsers)
                }

                return Resource.Success(usersResponse ?: it)
            }
        }

        return Resource.Error(response.message())
    }

    private suspend fun safeGetUsers(userName: String) {
        users.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = dataRepository.findUserDetailByUsername(userName)
                users.postValue(handleGetUsers(response))
            }else {
                users.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> users.postValue(Resource.Error("Network Failure"))
                else -> users.postValue(Resource.Error("Conversion Error"))
            }
        }
    }



    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<GithubApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activityNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activityNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    NetworkCapabilities.TRANSPORT_ETHERNET -> true
                    else -> false
                }
            }
        }

        return false
    }
}