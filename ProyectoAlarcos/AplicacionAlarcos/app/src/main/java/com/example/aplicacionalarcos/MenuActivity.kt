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
import android.graphics.drawable.ColorDrawable
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat

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
            binding.AjustesButton,
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

        binding.InfoNutriButton.setOnClickListener {
            Toast.makeText(this, getString(R.string.Implementando), Toast.LENGTH_SHORT).show()
            //  val intent = Intent(this, InfoNutriActivity::class.java)
            //    startActivity(intent)
            //    // Agregar animación
            //    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.UltimasComidasButton.setOnClickListener {
            Toast.makeText(this, getString(R.string.Implementando), Toast.LENGTH_SHORT).show()
        //    val intent = Intent(this, UltimasComidasActivity::class.java)
        //    startActivity(intent)
        //    // Agregar animación
        //    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


            binding.obAtras.setOnClickListener {

                showDialog()
            }


        binding.AjustesButton.setOnClickListener {
           navigateToPrefilActivity()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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



    private fun showDialog() {
        val email = auth.currentUser?.email // Obtiene el correo del usuario actual

        val dialog = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.cerrar_sesi_n))
            setMessage(
                getString(R.string.se_cerrar_la_sesi_n_con_el_correo) + " $email " + getString(
                    R.string.desea_continuar
                )
            )
            setPositiveButton(R.string.aceptar) { _, _ ->
                // Acción para cerrar sesión y volver al login
                val intent = Intent(this@MenuActivity, MainActivity::class.java)
                auth.signOut() // Cierra la sesión
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            setNegativeButton(getString(R.string.cancelar)) { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
        }.create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            // Cambiar el color de texto de los botones
            val buttonColor = ContextCompat.getColor(this, R.color.VerdeFont) // Color definido
            positiveButton.setTextColor(buttonColor)
            negativeButton.setTextColor(buttonColor)

            // Cambiar el fondo del diálogo
            val backgroundColor = ContextCompat.getColor(this, R.color.swicth) // Color de fondo
            dialog.window?.setBackgroundDrawable(ColorDrawable(backgroundColor))
        }

        dialog.show()
    }




    private fun navigateToPrefilActivity() {
        val intent = Intent(this, AjustesActivity::class.java)
        startActivity(intent)
        // Agregar animación
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    // Navegamos a la pantalla de IMC
    private fun navigateToImcActivity() {
        val intent = Intent(this, ImcActivity::class.java)
        startActivity(intent)
        // Agregar animación
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        Toast.makeText(this, getString(R.string.a_calcular_el_imc), Toast.LENGTH_SHORT).show()
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
