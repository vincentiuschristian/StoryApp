package com.example.storyapp.view.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.response.LoginResult
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.view.main.MainActivity

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

    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()
            val saveToken = LoginResult().token
            loginViewModel.saveSession(UserModel(email, saveToken.toString()))

            when {
                email.isEmpty() -> {
                    binding.textInputLayoutEmail.error = "Masukan Email"
                }
                pass.isEmpty() -> {
                    binding.textInputLayoutPass.error = "Masukan Password"
                }
                else -> {
                    login(email, pass)
                    showAlert("Login Berhasil", "Halo!"){ _, _ ->
                        navigate()
                    }

                }
            }


        }
    }

    private fun login(email: String, pass: String){
        loginViewModel.loginUser(email, pass).observe(this){result ->
            if (result != null){
                when(result){
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showToast(result.data.message)
                        showLoading(false)
                    }

                    is ResultState.Error -> {
                        showToast(result.error)
                        Toast.makeText(this, "Register Gagal", Toast.LENGTH_SHORT).show()
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

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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