package com.example.storyapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.adapter.StoryAdapter
import com.example.storyapp.view.insertStory.InsertStoryActivity
import com.example.storyapp.view.maps.MapsActivity
import com.example.storyapp.view.setting.SettingActivity
import com.example.storyapp.view.welcome.WelcomeActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adapter = StoryAdapter()
        binding.rvItem.layoutManager = GridLayoutManager(applicationContext, 2)
        binding.rvItem.adapter = adapter

        setupView()
        getSession()

        binding.apply {
            refresh.setOnRefreshListener {
                getStories()
                refresh.isRefreshing = false
            }
        }

        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(applicationContext, InsertStoryActivity::class.java))
        }

        getStories()
    }

    private fun getSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(applicationContext, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.title = resources.getString(R.string.story)
    }

    private fun getStories() {
        showLoading(true)
        binding.tvEmptyStory.visibility = View.VISIBLE
        viewModel.getAllStories.observe(this) {
            adapter.submitData(lifecycle, it)
            showLoading(false)
            binding.tvEmptyStory.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_Logout -> {
            viewModel.logout()
            true
        }

        R.id.menu_setting -> {
            startActivity(Intent(applicationContext, SettingActivity::class.java))
            true
        }

        R.id.menu_maps -> {
            startActivity(Intent(applicationContext, MapsActivity::class.java))
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    private fun showSnackbar(message: String?) {
        val snackBar = Snackbar.make(binding.root, message!!, Snackbar.LENGTH_SHORT)
        snackBar.show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}