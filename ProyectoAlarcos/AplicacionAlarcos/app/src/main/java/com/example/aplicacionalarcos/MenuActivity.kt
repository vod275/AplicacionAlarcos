package com.example.aplicacionalarcos

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.aplicacionalarcos.fragmentInfo.ViewTab

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var auth: FirebaseAuth // Declarar FirebaseAuth
    private var menuAbierto = false // Estado del menú

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
            binding.ibInfo,
            binding.obAtras,
            binding.AddIngredienteButton
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
            val intent = Intent(this, UltimasComidasActivity::class.java)
            startActivity(intent)
           // Agregar animación
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.AddIngredienteButton.setOnClickListener {
            val intent = Intent(this, ActivityIngredientes::class.java)
            startActivity(intent)
            // Agregar animación
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.NuevasComidas.setOnClickListener(){
            val intent = Intent(this, NuevasComidas::class.java)
            startActivity(intent)
            // Agregar animación
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
            binding.obAtras.setOnClickListener {

                showDialog()
            }


        binding.AjustesButton.setOnClickListener {
           navigateToPrefilActivity()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.ibInfo.setOnClickListener{
            val intent = Intent(this, ViewTab::class.java)
            startActivity(intent)
            // Agregar animación
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.VideoButton.setOnClickListener {
            val intent = Intent(this, VideoTutorial::class.java)
            startActivity(intent)
            // Agregar animación
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.ibMenu.setOnClickListener {
            if (menuAbierto) {
                cerrarMenu()
            } else {
                abrirMenu()
            }
            menuAbierto = !menuAbierto
        }

        binding.InfoNutriButton.setOnClickListener{
            val intent = Intent(this, InformacionNutricionalActivity::class.java)
            startActivity(intent)
            // Agregar animación
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
        dialogBinding.btnAccept.setOnClickListener {
            dialog.dismiss()
            navigateToImcActivity() // Navegar a la pantalla de IMC después de aceptar el diálogo
        }

        dialog.show()
    }


   //Dialogo cuando sales de la aplicacion al login
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


    //Ir a los ajustes
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

    // Función para iniciar las Animaciones
    private fun iniciarAnimacion(button: View) {
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


    private fun abrirMenu() {
        binding.ibInfo.visibility = View.VISIBLE
        binding.AjustesButton.visibility = View.VISIBLE
       binding.VideoButton.visibility = View.VISIBLE

        val animInfo = ObjectAnimator.ofFloat(binding.ibInfo, "translationX", 0f, -370f)
        val animAjustes = ObjectAnimator.ofFloat(binding.AjustesButton, "translationX", 0f, -170f)
        val animVideo = ObjectAnimator.ofFloat(binding.VideoButton, "translationX", 0f, -570f)
        animInfo.duration = 300
        animAjustes.duration = 300
        animVideo.duration = 300

        animInfo.start()
        animAjustes.start()
        animVideo.start()
    }

    private fun cerrarMenu() {
        val animInfo = ObjectAnimator.ofFloat(binding.ibInfo, "translationX", -370f, 0f)
        val animAjustes = ObjectAnimator.ofFloat(binding.AjustesButton, "translationX", -170f, 0f)
        val animVideo = ObjectAnimator.ofFloat(binding.VideoButton, "translationX", -570f, 0f)

        animInfo.duration = 300
        animAjustes.duration = 300
        animVideo.duration = 300

        animInfo.start()
        animAjustes.start()
        animVideo.start()

        // Retrasa la invisibilidad para que la animación se vea
        binding.ibInfo.postDelayed({ binding.ibInfo.visibility = View.INVISIBLE }, 300)
        binding.AjustesButton.postDelayed({ binding.AjustesButton.visibility = View.INVISIBLE }, 300)
        binding.VideoButton.postDelayed({ binding.VideoButton.visibility = View.INVISIBLE }, 300)
    }
}
