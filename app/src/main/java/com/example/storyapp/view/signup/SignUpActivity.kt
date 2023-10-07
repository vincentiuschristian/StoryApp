package com.example.storyapp.view.signup

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.data.ResultState
import com.example.storyapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private val signUpViewModel: SignUpViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.btnSignUp.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()

            when {
                name.isEmpty() -> {
                    binding.textInputLayoutUsername.error = "Masukan Nama"
                }
                email.isEmpty() -> {
                    binding.textInputLayoutEmail.error = "Masukan Email"
                }
                pass.isEmpty() -> {
                    binding.textInputLayoutPass.error = "Masukan Password"
                }
                else -> {
                    signUp(name, email, pass)
                    showAlert("Register Berhasil", "Welcome")
                }
            }


        }
    }

    private fun signUp(name: String, email: String, pass: String){
        signUpViewModel.register(name, email, pass).observe(this){result ->
            if (result != null){
                when(result){
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showToast(result.data.message)
                        Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show()
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
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

}