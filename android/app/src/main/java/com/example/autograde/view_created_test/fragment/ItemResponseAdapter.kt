package com.example.autograde.view_created_test.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autograde.data.api.response.UserTestsItem
import com.example.autograde.databinding.ItemListResponseBinding

class ItemResponseAdapter (private val onItemClicked : (UserTestsItem) -> Unit ) : ListAdapter<UserTestsItem, ItemResponseAdapter.MyViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserTestsItem>() {
            override fun areItemsTheSame(
                oldItem: UserTestsItem,
                newItem: UserTestsItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: UserTestsItem,
                newItem: UserTestsItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class MyViewHolder (val binding : ItemListResponseBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (userTestsItem: UserTestsItem ){
            binding.tvName.text = userTestsItem.username
            binding.tvScore.text = userTestsItem.totalGrade.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListResponseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val question = getItem(position)
        holder.bind(question)
        holder.itemView.setOnClickListener{
            onItemClicked(question)
        }

    }

}