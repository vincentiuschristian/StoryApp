package com.example.storyapp.view.signup

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
import com.example.storyapp.databinding.ActivitySignUpBinding
import com.example.storyapp.view.login.LoginActivity
import com.google.android.material.snackbar.Snackbar

class SignUpActivity : AppCompatActivity() {
    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private val signUpViewModel: SignUpViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupSignUp()
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

    private fun setupSignUp() {
        binding.btnSignUp.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()
            signUp(name, email, pass)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageview, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val etName = ObjectAnimator.ofFloat(binding.textInputLayoutUsername, View.ALPHA, 1f).setDuration(200)
        val etEmail = ObjectAnimator.ofFloat(binding.textInputLayoutEmail, View.ALPHA, 1f).setDuration(200)
        val etPass = ObjectAnimator.ofFloat(binding.textInputLayoutPass, View.ALPHA, 1f).setDuration(200)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                etName,
                etEmail,
                etPass,
                btnLogin
            )
            startDelay = 250
        }.start()
    }

    private fun signUp(name: String, email: String, pass: String){
        signUpViewModel.register(name, email, pass).observe(this){result ->
            if (result != null){
                when(result){
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showSnackbar(result.data.message)
                        showAlert(resources.getString(R.string.SuccessRegister), resources.getString(R.string.RegisMsg)){ _, _ ->
                            navigate()
                        }
                        showLoading(false)
                    }

                    is ResultState.Error -> {
                        showAlert(resources.getString(R.string.register_failed), resources.getString(R.string.FailedMsg, result.error))
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

    private fun showSnackbar(message: String?) {
        Snackbar.make(binding.root, message!!, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigate(){
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }
}