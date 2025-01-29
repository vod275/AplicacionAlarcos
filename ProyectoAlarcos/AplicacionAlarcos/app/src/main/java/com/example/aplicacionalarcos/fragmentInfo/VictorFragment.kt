package com.example.aplicacionalarcos.fragmentInfo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aplicacionalarcos.MenuActivity
import com.example.aplicacionalarcos.R
import com.example.aplicacionalarcos.databinding.ActivityVictorFragmentBinding

class VictorFragment : Fragment() {
    private var _binding: ActivityVictorFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ActivityVictorFragmentBinding.inflate(inflater, container, false)
        // Botón atrás
        binding.obAtras2.setOnClickListener {
            val intent = Intent(requireContext(), MenuActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            requireActivity().finish() // Cierra la actividad actual si es necesario
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvVictor.text =
            getString(R.string.descripci_n_hola_me_llamo_v_ctor_oliver_soy_estudiante_de_programacion_y_tengo_27_a_os).trimIndent()




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
