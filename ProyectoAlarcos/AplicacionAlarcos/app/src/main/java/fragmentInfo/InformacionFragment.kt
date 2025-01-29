package fragmentInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aplicacionalarcos.databinding.ActivityInformacionFragmentBinding


class InformacionFragment : Fragment() {
    private var _binding: ActivityInformacionFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ActivityInformacionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvInfo.text = """
            Descripción: HealthyBites es una app para llevar un mejor control de tu alimentación 
            de forma sana y saludable.

            HealthyBites 
            Versión: 1.0 
            Fecha: 2024 
            Autores: Manuel Pérez Sánchez y Víctor Oliver Donoso
        """.trimIndent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
