package com.example.aplicacionalarcos

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionalarcos.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        //auth.signOut() // Cierra sesión al inicia (para boton de cerrar sesion)

        // Configura One Tap para Google Sign-In
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                register(email, password)
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                    updateUI(user)
                } else {
                    Log.w("Register", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Error al registrar: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun signInWithGoogle() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                signInLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent).build())
            }
            .addOnFailureListener { e ->
                Log.w("GoogleSignIn", "One Tap Sign-In failed", e)
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            }
    }

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                updateUI(user)
                            } else {
                                Log.w("GoogleSignIn", "signInWithCredential:failure", task.exception)
                                Toast.makeText(
                                    this,
                                    "Error al autenticar con Google",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val refreshedUser = auth.currentUser
                    if (refreshedUser != null) {
                        Toast.makeText(this, "Bienvenido: ${refreshedUser.email}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, DatosUsuarioActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Usuario no encontrado, inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
                        auth.signOut()
                    }
                } else {
                    Toast.makeText(this, "Error al verificar usuario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                }
            }
        } else {
            Toast.makeText(this, "Por favor, inicia sesión o regístrate", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

}