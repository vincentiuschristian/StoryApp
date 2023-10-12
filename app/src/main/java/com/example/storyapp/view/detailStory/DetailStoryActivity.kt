package com.example.storyapp.view.detailStory

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.util.dateFormatter
import java.util.TimeZone

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_story)

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val detail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_DATA, ListStoryItem::class.java)
        } else {
            intent.getParcelableExtra(KEY_DATA)
        }

        binding.apply {
            tvNameDetail.text = detail?.name
            tvDescDetail.text = detail?.description
            tvTime.text = dateFormatter(detail?.createdAt, TimeZone.getDefault().id)
            Glide.with(root.context)
                .load(detail?.photoUrl)
                .into(ivImage)
        }
    }

    companion object {
        const val KEY_DATA = "key_data"
    }
}