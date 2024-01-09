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
import com.example.storyapp.view.signup.SignUpActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupLogin()
        setupView()
        playAnimation()

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
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
        supportActionBar?.hide()
    }

    private fun setupLogin() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmailLogin.toString()
            val pass = binding.edtPasswordLogin.toString()
            login(email, pass)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageview, View.TRANSLATION_X, -40f, 40f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val textLogin =
            ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(150)
        val etEmail =
            ObjectAnimator.ofFloat(binding.textInputLayoutEmailLog, View.ALPHA, 1f).setDuration(150)
        val etPass =
            ObjectAnimator.ofFloat(binding.textInputLayoutPassLog, View.ALPHA, 1f).setDuration(150)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(150)
        val textSignUp = ObjectAnimator.ofFloat(binding.textSignUp, View.ALPHA, 1f).setDuration(150)
        val btnSignUp = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(150)

        AnimatorSet().apply {
            playSequentially(
                textLogin,
                etEmail,
                etPass,
                btnLogin,
                textSignUp,
                btnSignUp
            )
            startDelay = 250
        }.start()
    }

    private fun login(email: String, pass: String) {
        loginViewModel.loginUser(email, pass).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showSnackbar(result.data.message)
                        showAlert(
                            resources.getString(R.string.Success),
                            resources.getString(R.string.SuccessMsg)
                        ) { _, _ ->
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

    private fun showAlert(
        title: String,
        message: String,
        onPositiveClick: ((dialog: DialogInterface, which: Int) -> Unit)? = null
    ) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(resources.getString(R.string.next_activity), onPositiveClick)
            create()
            show()
        }
    }

    private fun saveToken(token: LoginResponse) {
        val tokenValue = token.loginResult.token
        if (tokenValue.isNotEmpty()) {
            loginViewModel.saveSession(UserModel(token.loginResult.token))
        } else {
            showSnackbar(token.message)
        }
    }

    private fun showSnackbar(message: String?) {
        Snackbar.make(binding.root, message!!, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigate() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}