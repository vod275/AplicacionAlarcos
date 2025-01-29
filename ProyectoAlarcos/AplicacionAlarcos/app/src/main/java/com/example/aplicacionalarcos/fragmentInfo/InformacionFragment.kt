package com.example.aplicacionalarcos.fragmentInfo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aplicacionalarcos.MenuActivity
import com.example.aplicacionalarcos.R
import com.example.aplicacionalarcos.databinding.ActivityInformacionFragmentBinding


class InformacionFragment : Fragment() {
    private var _binding: ActivityInformacionFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ActivityInformacionFragmentBinding.inflate(inflater, container, false)
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

        binding.tvInfo.text =
            getString(R.string.descripci_n_healthybites_es_una_app_para_llevar_un_mejor_control_de_tu_alimentaci_n_de_forma_sana_y_saludable_healthybites_versi_n_1_0_fecha_2024_autores_manuel_p_rez_s_nchez_y_v_ctor_oliver_donoso).trimIndent()



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
