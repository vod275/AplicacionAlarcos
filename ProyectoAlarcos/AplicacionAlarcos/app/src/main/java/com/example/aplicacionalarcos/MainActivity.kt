package com.example.aplicacionalarcos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacionalarcos.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.TwitterAuthProvider
import objetos.UserSession

private const val RC_SIGN_IN = 9001

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var googleSignInClient: GoogleSignInClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Configurar Google Sign-In (One Tap y estándar)
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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)



        // Acción del botón de Google Sign-In
        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Acción del botón de Login por correo
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.editText?.text.toString().trim()
            val password = binding.passwordInput.editText?.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signIn(email, password)
                } else {
                    Toast.makeText(this, "Por favor, ingresa un correo válido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Acción del botón de Registro
        binding.registerButton.setOnClickListener {
            val email = binding.emailInput.editText?.text.toString().trim()
            val password = binding.passwordInput.editText?.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                register(email, password)
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción del botón de Twitter Sign-In
        binding.XSignInButton.setOnClickListener {
            signInWithTwitter()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    // Método de autenticación con correo y contraseña
    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    // Método de registro con correo y contraseña
    private fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, getString(R.string.usuario_registrado_exitosamente), Toast.LENGTH_SHORT).show()
                    updateUI(user)
                } else {
                    val errorMessage = task.exception?.message ?: getString(R.string.error_desconocido_intenta_nuevamente)
                    Log.w("Register", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, getString(R.string.error_al_registrar) + errorMessage, Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    // Método para iniciar sesión con Google
    private fun signInWithGoogle() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    signInLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent).build())
                } catch (e: android.content.IntentSender.SendIntentException) {
                    Log.e("GoogleSignIn", "Couldn't start One Tap UI:" + e.localizedMessage)
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                }
            }
            .addOnFailureListener { e ->
                Log.w("GoogleSignIn", "One Tap Sign-In failed", e)
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
    }

    private fun signInWithTwitter() {
        // Crea el proveedor OAuth para Twitter
        val provider = OAuthProvider.newBuilder("twitter.com")

        // Inicia la autenticación con Twitter
        auth.startActivityForSignInWithProvider(this, provider.build())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Accedemos al credential que contiene el token y el secreto
                    val credential = task.result?.credential

                    // Verificamos que el credential sea un OAuthCredential
                    if (credential is OAuthCredential) {
                        // Obtén los tokens desde el credential de OAuth
                        val twitterAuthToken = credential.accessToken // El accessToken
                        val twitterAuthSecret = credential.secret // El accessTokenSecret

                        // Verificamos que los tokens no sean nulos antes de crear el credential de Firebase
                        if (!twitterAuthToken.isNullOrEmpty() && !twitterAuthSecret.isNullOrEmpty()) {
                            // Creamos el credential de Firebase utilizando estos tokens
                            val firebaseCredential = TwitterAuthProvider.getCredential(
                                twitterAuthToken, // El accessToken
                                twitterAuthSecret  // El accessTokenSecret
                            )

                            // Autenticar con Firebase utilizando las credenciales de Twitter
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener { signInTask ->
                                    if (signInTask.isSuccessful) {
                                        // Si la autenticación fue exitosa, obtenemos el usuario autenticado
                                        val user = auth.currentUser
                                        updateUI(user)
                                    } else {
                                        // Si hubo un error en la autenticación de Firebase, mostramos un mensaje
                                        Log.e("TwitterSignIn", "Error al autenticar con Firebase", signInTask.exception)
                                        Toast.makeText(this, "Error al autenticar con Firebase", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Log.e("TwitterSignIn", "Tokens de Twitter son nulos o vacíos")
                            Toast.makeText(this, "Error al obtener los tokens de Twitter", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("TwitterSignIn", "Credential no es de tipo OAuthCredential")
                        Toast.makeText(this, "Error al autenticar con Twitter", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Si hubo un error en la autenticación con Twitter, mostramos un mensaje
                    Log.e("TwitterSignIn", "Error al autenticar con Twitter", task.exception)
                    Toast.makeText(this, "Error al autenticar con Twitter", Toast.LENGTH_SHORT).show()
                }
            }
    }





    // Lanzador de actividad para manejar el resultado de la autenticación
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
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
                            Toast.makeText(this, getString(R.string.error_al_autenticar_con_google), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Recarga la información del usuario para asegurarse de tener la más reciente
            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val refreshedUser = auth.currentUser
                    if (refreshedUser != null) {
                        // Recuperamos el nombre de usuario  si está disponible
                        val twitterUsername = refreshedUser.displayName ?: "Nombre no disponible"

                        // Guardamos el nombre de usuario en la sesión de usuario
                        UserSession.nombre = twitterUsername // Guardamos el nombre en la sesión

                        // Mostramos el nombre de usuario de Twitter en un Toast
                        Toast.makeText(this, "Bienvenido: $twitterUsername", Toast.LENGTH_SHORT).show()

                        // Aquí mantienes el flujo original de la aplicación
                        val intent = Intent(this, MenuActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, getString(R.string.usuario_no_encontrado_inicia_sesi_n_nuevamente), Toast.LENGTH_SHORT).show()
                        auth.signOut()
                    }
                } else {
                    Toast.makeText(this, getString(R.string.error_al_verificar_usuario) + task.exception?.message, Toast.LENGTH_SHORT).show()
                    auth.signOut()
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.por_favor_inicia_sesi_n_o_reg_strate), Toast.LENGTH_SHORT).show()
        }
    }





    // Comprobamos el estado del usuario cuando la actividad se inicia
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
}
