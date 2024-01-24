package com.example.mystoryapp.ui.viewmodel

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapp.data.response.ListStoryItem
import com.example.mystoryapp.databinding.StoryListItemBinding
import com.example.mystoryapp.ui.DetailStoryActivity
import java.text.SimpleDateFormat
import java.util.*

class StoryAdapter :
    PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK)
    {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    class MyViewHolder(private val binding: StoryListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem){
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = inputFormat.parse("${story.createdAt}")
            val finalDate = outputFormat.format(date)

            binding.tvItemName.text = "${story.name}"
            binding.datePost.text =  "${finalDate}"
            binding.descriptionText.text = "${story.description}"
            binding.ivItemPhoto?.let {
                Glide.with(it.context)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(binding.ivItemPhoto)
            }

            val storyId = story.id

            binding.storyCardview.setOnClickListener {
                val intent = Intent(binding.root.context, DetailStoryActivity::class.java)
                intent.putExtra("idStory", storyId)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.tvItemName, "name"),
                        Pair(binding.ivItemPhoto, "photo_profile"),
                        Pair(binding.descriptionText, "description"),
                        Pair(binding.datePost, "date")
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }

        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}