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
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.adapter.StoryAdapter
import com.example.storyapp.view.insertStory.InsertStoryActivity
import com.example.storyapp.view.setting.SettingActivity
import com.example.storyapp.view.welcome.WelcomeActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = GridLayoutManager(this, 2)
        binding.rvItem.layoutManager = layoutManager
        binding.rvItem.setHasFixedSize(true)

        setupView()
        getSession()

        binding.apply {
            refresh.setOnRefreshListener {
                getStories()
                refresh.isRefreshing = false
            }
        }

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, InsertStoryActivity::class.java)
            startActivity(intent)
        }

        getStories()
    }

    private fun getSession(){
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
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
        supportActionBar?.title = "Story"
    }

    private fun setUpAction(data: List<ListStoryItem>?) {
        val adapter = StoryAdapter()
        adapter.submitList(data)
        binding.rvItem.adapter = adapter
    }


    private fun getStories() {
        viewModel.getAllStories().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showSnackbar(result.data.message)
                        showLoading(false)
                        setUpAction(result.data.listStory)
                    }

                    is ResultState.Error -> {
                        showSnackbar(result.error)
                        showLoading(false)
                    }
                }
            }
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
            val intentSetting = Intent(this, SettingActivity::class.java)
            startActivity(intentSetting)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    private fun showSnackbar(message: String?){
        val snackBar = Snackbar.make(binding.root, message!!, Snackbar.LENGTH_SHORT)
        snackBar.show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}