package com.dicoding.picodiploma.storyappdicoding.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.storyappdicoding.R
import com.dicoding.picodiploma.storyappdicoding.view.detail.DetailActivity
import com.dicoding.picodiploma.storyappdicoding.data.response.ListStoryItem

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story?.id != null) {
            holder.bind(story)
        } else {
            Log.e("StoryAdapter", "Invalid story ID: ${story?.id}")
        }
    }

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgPhoto: ImageView = itemView.findViewById(R.id.ivStory)
        private val ownerName: TextView = itemView.findViewById(R.id.txt_story_uname)
        private val description: TextView = itemView.findViewById(R.id.txt_story_description)

        fun bind(story: ListStoryItem) {
            ownerName.text = story.name
            description.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(imgPhoto)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.STORY_ID, story.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(imgPhoto, "image"),
                        Pair(ownerName, "ownerName"),
                        Pair(description, "description")
                    )

                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

