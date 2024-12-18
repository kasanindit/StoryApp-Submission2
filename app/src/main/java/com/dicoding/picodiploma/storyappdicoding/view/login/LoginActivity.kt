package com.dicoding.picodiploma.storyappdicoding.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.storyappdicoding.R
import com.dicoding.picodiploma.storyappdicoding.data.pref.UserModel
import com.dicoding.picodiploma.storyappdicoding.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.storyappdicoding.view.customview.PasswordEditText
import com.dicoding.picodiploma.storyappdicoding.view.ViewModelFactory
import com.dicoding.picodiploma.storyappdicoding.view.main.MainActivity
import com.dicoding.picodiploma.storyappdicoding.view.signup.SignupActivity
import com.dicoding.picodiploma.storyappdicoding.view.welcome.WelcomeActivity

class LoginActivity : AppCompatActivity() {
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    private lateinit var passwordEditText: PasswordEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        observeLogin()
        playAnimation()
        setupAction()

        passwordEditText = findViewById(R.id.ed_login_password)
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.toRegister.setOnClickListener {
            val intent = Intent (this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
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

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showLoading(true)
            loginViewModel.login(email, password)
        }
    }

    private fun observeLogin() {
        loginViewModel.loginResult.observe(this) { result ->
            showLoading(false)
            result.onSuccess { response ->
                val loginResult = response.loginResult
                if (response.error == false && loginResult != null && !loginResult.token.isNullOrEmpty()) {
                    showLoading(true)

                    val user = UserModel(
                        email = loginResult.name ?: "Unknown",
                        token = loginResult.token,
                        isLogin = true
                    )
                    loginViewModel.saveSession(user)
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    val intentToWelcome = Intent(this, MainActivity::class.java)
                    startActivity(intentToWelcome)
                    finish()
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Akun belum terdaftar. Daftar sekarang!", Toast.LENGTH_SHORT).show()
                    val intentToSignup = Intent(this, SignupActivity::class.java)
                    startActivity(intentToSignup)
                }
            }.onFailure {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val toRegister = ObjectAnimator.ofFloat(binding.layoutToRegister, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login,
                toRegister
            )
            startDelay = 100
        }.start()
    }

    private fun setMyButtonEnable(){
        val result = passwordEditText.text
        binding.loginButton.isEnabled = result != null && result.toString().isNotEmpty()
    }

    private fun showLoading(isLoading: Boolean) {
        runOnUiThread{ binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intentToWelcome = Intent (this@LoginActivity, WelcomeActivity::class.java)
        startActivity(intentToWelcome)
    }

}