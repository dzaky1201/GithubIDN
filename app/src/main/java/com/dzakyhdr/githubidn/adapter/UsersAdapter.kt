package com.dzakyhdr.githubidn.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dzakyhdr.githubidn.R
import com.dzakyhdr.githubidn.model.Item
import kotlinx.android.synthetic.main.item_list.view.*

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(user.avatar_url).into(img_profile)
            txt_username.text = user.login
//            total_follower.text = user.followers_url
//            total_following.text = user.following_url
//            total_repo.text = user.repos_url
        }

    }

    override fun getItemCount(): Int = differ.currentList.size

}