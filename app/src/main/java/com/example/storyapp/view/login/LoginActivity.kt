package com.example.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.response.LoginResponse
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.view.main.MainActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupView()
        playAnimation()

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
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()
            login(email, pass)
//            when {
//                email.isEmpty() -> {
//                    binding.textInputLayoutEmail.error = resources.getString(R.string.eName)
//                }
//                pass.isEmpty() -> {
//                    binding.textInputLayoutPass.error = resources.getString(R.string.ePass)
//                }
//                else -> {
//
//                }
//            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageview, View.TRANSLATION_X, -40f, 40f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val etEmail = ObjectAnimator.ofFloat(binding.textInputLayoutEmail, View.ALPHA, 1f).setDuration(100)
        val etPass = ObjectAnimator.ofFloat(binding.textInputLayoutPass, View.ALPHA, 1f).setDuration(100)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                etEmail,
                etPass,
                btnLogin
            )
            startDelay = 250
        }.start()
    }

    private fun login(email: String, pass: String){
        loginViewModel.loginUser(email, pass).observe(this){result ->
            if (result != null){
                when(result){
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showSnackbar(result.data.message)
                        showAlert(resources.getString(R.string.Success), resources.getString(R.string.SuccessMsg)){ _, _ ->
                            navigate()
                        }
                        saveToken(result.data)
                        showLoading(false)
                    }

                    is ResultState.Error -> {
                        showSnackbar(result.error)
                        showLoading(false)
                    }
                }
            }
        }
    }


    private fun showAlert(title: String, message: String, onPositiveClick: ((dialog: DialogInterface, which: Int) -> Unit)? = null) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK", onPositiveClick)
            create()
            show()
        }
    }

    private fun saveToken(token : LoginResponse){
        val email = binding.edtEmail.text.toString()
        val tokenValue = token.loginResult.token
        if (!tokenValue.isNullOrEmpty()){
            loginViewModel.saveSession(UserModel(email, token.loginResult.token))
            showSnackbar(token.loginResult.toString())
        } else {
            showSnackbar(token.message)
        }
    }

    private fun showSnackbar(message: String?){
        Snackbar.make(binding.root, message!!, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigate(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}