package com.dicoding.picodiploma.storyappdicoding.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.storyappdicoding.R
import com.dicoding.picodiploma.storyappdicoding.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.storyappdicoding.view.ViewModelFactory
import com.dicoding.picodiploma.storyappdicoding.view.customview.PasswordEditText
import com.dicoding.picodiploma.storyappdicoding.view.login.LoginActivity
import com.dicoding.picodiploma.storyappdicoding.view.welcome.WelcomeActivity
import java.net.SocketTimeoutException

class SignupActivity : AppCompatActivity() {
    private val signUpViewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignupBinding
    private lateinit var passwordEditText: PasswordEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        binding.toLogin.setOnClickListener {
            val intent = Intent (this@SignupActivity, LoginActivity::class.java)
            showLoading(true)
            startActivity(intent)
        }

        passwordEditText = findViewById(R.id.ed_register_password)
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
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
        obeserveSignup()

        binding.signupButton.setOnClickListener {
            val email = binding.edRegisterEmail.text.toString()
            val name = binding.edRegisterName.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading(true)
            signUpViewModel.signup(name, email, password)
        }
    }

    private fun obeserveSignup(){
        signUpViewModel.signupResult.observe(this) { result ->
            showLoading(false)
            result.onSuccess { response ->
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                if (response.message == "Akun berhasil dibuat!") {
                    signUpViewModel.signupResult.removeObservers(this)
                    val intentToLogin = Intent(this@SignupActivity, LoginActivity::class.java)
                    startActivity(intentToLogin)
                    finish()
                }
            }.onFailure { exception ->
                val errorMessage = when (exception) {
                    is SocketTimeoutException -> "Koneksi timeout. Mohon coba lagi."
                    else -> exception.message ?: "Terjadi kesalahan."
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
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
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        val toLogin = ObjectAnimator.ofFloat(binding.layoutToLogin, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup,
                toLogin
            )
            startDelay = 100
        }.start()
    }

    private fun setMyButtonEnable(){
        val result = passwordEditText.text
        binding.signupButton.isEnabled = (result != null) && result.toString().isNotEmpty()
    }

    private fun showLoading(isLoading: Boolean) {
        runOnUiThread{ binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intentToWelcome = Intent (this@SignupActivity, WelcomeActivity::class.java)
        startActivity(intentToWelcome)
    }
}