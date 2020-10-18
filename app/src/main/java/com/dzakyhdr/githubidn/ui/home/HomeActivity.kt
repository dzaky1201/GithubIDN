package com.dzakyhdr.githubidn.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dzakyhdr.githubidn.R
import com.dzakyhdr.githubidn.adapter.UsersAdapter
import com.dzakyhdr.githubidn.repository.DataRepository
import com.dzakyhdr.githubidn.util.Constans.Companion.QUERY_PAGE_SIZE
import com.dzakyhdr.githubidn.util.Resource
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var usersAdapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val dataRepository = DataRepository()
        val viewModelFactory = HomeViewModelFactory(application, dataRepository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        setupRecyclerView()

        viewModel.users.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        usersAdapter.differ.submitList(it.items)
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message.let {
                        Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        search_user.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.getUsers(newText)
                return false
            }
        })

    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    //untuk loading progressbar
    private fun showProgressBar(){
        loading.visibility = View.VISIBLE
        isLoading = true
    }

    //untuk loading progressbar
    private fun hideProgressBar(){
        loading.visibility = View.GONE
        isLoading = false
    }

    private fun setupRecyclerView() {
        usersAdapter = UsersAdapter()
        rv_github.apply {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(this@HomeActivity)
            addOnScrollListener(this@HomeActivity.scrollListener)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotBegining = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotBegining &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate){
                viewModel.getUsers("dzaky1201")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }

        }


    }
}