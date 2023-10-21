package com.example.storyapp.view.detailStory

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

        // ubah ini deprecated code !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        val detail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_DATA, StoryEntity::class.java)
        } else {
            intent.getParcelableExtra(KEY_DATA)
        }

        binding.apply {
            tvNameDetail.text = detail?.name
            tvDescDetail.text = detail?.description
            tvTime.text = dateFormatter(detail?.createdAt, TimeZone.getDefault().id)
            tvLocation.text = resources.getString(R.string.location, detail?.lat.toString(), detail?.lon.toString())
            Glide.with(root.context)
                .load(detail?.photoUrl)
                .into(ivImage)
        }
    }

    companion object {
        const val KEY_DATA = "key_data"
    }
}