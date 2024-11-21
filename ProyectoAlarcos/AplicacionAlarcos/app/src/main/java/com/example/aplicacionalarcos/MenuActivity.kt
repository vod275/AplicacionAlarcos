package com.example.aplicacionalarcos

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionalarcos.databinding.ActivityMensajeMotivacionalBinding
import com.example.aplicacionalarcos.databinding.ActivityMenuBinding
import com.google.firebase.auth.FirebaseAuth
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.animation.AccelerateDecelerateInterpolator

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var auth: FirebaseAuth // Declarar FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Lista de botones en el menú
        val buttons = listOf(
            binding.IMCButton,
            binding.InfoNutriButton,
            binding.UltimasComidasButton,
            binding.obAtras
            // Añade más botones aquí, como binding.InfoNutriButton o binding.UltimasComidasButton si están en tu layout
        )

        // Aplicar animaciones a todos los botones con un retraso
        buttons.forEachIndexed { index, button ->
            button.postDelayed({
                iniciarAnimacion(button) // Inicia la animación personalizada para cada botón
            }, (index * 200).toLong()) // Añade un retraso para cada botón
        }

        // Botón para mostrar el cuadro de diálogo motivacional y calcular IMC
        binding.IMCButton.setOnClickListener {
            showMotivationalDialog()
        }

        //binding.InfoNutriButton.setOnClickListener {
            //  val intent = Intent(this, InfoNutriActivity::class.java)
            //    startActivity(intent)
            //    // Agregar animación
            //    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        //}

        //binding.UltimasComidasButton.setOnClickListener {
        //    val intent = Intent(this, UltimasComidasActivity::class.java)
        //    startActivity(intent)
        //    // Agregar animación
        //    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        //}

        // Botón para regresar al login
        binding.obAtras.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            auth.signOut() // Cierra la sesión del usuario
            startActivity(intent)
            // Agregar animación de regreso
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Agregar animación de regreso
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun showMotivationalDialog() {
        // Vinculamos el diseño del cuadro de diálogo
        val dialogBinding = ActivityMensajeMotivacionalBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)

        // Crear y mostrar el cuadro de diálogo
        val dialog = dialogBuilder.create()
        dialogBinding.btnAcceptDialog.setOnClickListener {
            dialog.dismiss()
            navigateToImcActivity() // Navegar a la pantalla de IMC después de aceptar el diálogo
        }

        dialog.show()
    }

    // Navegamos a la pantalla de IMC
    private fun navigateToImcActivity() {
        val intent = Intent(this, ImcActivity::class.java)
        startActivity(intent)
        // Agregar animación
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        Toast.makeText(this, "¡A calcular el IMC!", Toast.LENGTH_SHORT).show()
    }

    private fun iniciarAnimacion(button: Button) {
        // Animación de escala (aumenta y vuelve al tamaño original)
        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            button,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        )
        scaleUp.duration = 100

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            button,
            PropertyValuesHolder.ofFloat("scaleX", 1f),
            PropertyValuesHolder.ofFloat("scaleY", 1f)
        )
        scaleDown.duration = 100

        // Animación de deslizamiento (aparece desde la izquierda)
        val slideIn = ObjectAnimator.ofFloat(button, "translationX", -500f, 0f)
        slideIn.duration = 500
        slideIn.interpolator = AccelerateDecelerateInterpolator()

        // Combinar animaciones: deslizamiento + escala
        AnimatorSet().apply {
            play(slideIn).before(AnimatorSet().apply {
                play(scaleUp).before(scaleDown)
            })
            start()
        }
    }
}
