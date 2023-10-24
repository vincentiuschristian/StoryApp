package com.example.storyapp.view.detailStory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat.getParcelableExtra
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.paging.database.StoryEntity
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.util.dateFormatter
import java.util.TimeZone


class DetailStoryActivity : AppCompatActivity() {
    private val binding: ActivityDetailStoryBinding by lazy {
        ActivityDetailStoryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val detail = getParcelableExtra(intent, KEY_DATA, StoryEntity::class.java)

        binding.apply {
            tvNameDetail.text = detail?.name
            tvDescDetail.text = detail?.description
            tvTime.text = dateFormatter(detail?.createdAt, TimeZone.getDefault().id)
            tvLocation.text = resources.getString(
                R.string.location,
                detail?.lat.toString(),
                detail?.lon.toString()
            )
            Glide.with(root.context)
                .load(detail?.photoUrl)
                .into(ivImage)
        }
    }

    companion object {
        const val KEY_DATA = "key_data"
    }
}