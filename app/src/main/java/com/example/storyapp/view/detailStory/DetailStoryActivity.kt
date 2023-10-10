package com.example.storyapp.view.detailStory

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailStoryBinding

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_story)

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_DATA, ListStoryItem::class.java)
        } else {
            intent.getParcelableExtra(KEY_DATA)
        }

        setData(detail)
    }

    private fun setData(data: ListStoryItem?) {
        binding.apply {
            tvNameDetail.text = data?.name
            tvDescDetail.text = data?.description
            tvTime.text = data?.createdAt
            Glide.with(root.context)
                .load(data?.photoUrl)
                .into(ivImage)
        }
    }

    companion object {
        const val KEY_DATA = "key_data"
    }
}