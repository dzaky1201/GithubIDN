package com.dzakyhdr.githubidn.model

data class GithubResponse(
    val incomplete_results: Boolean,
    val items: MutableList<Item>,
    val total_count: Int
)