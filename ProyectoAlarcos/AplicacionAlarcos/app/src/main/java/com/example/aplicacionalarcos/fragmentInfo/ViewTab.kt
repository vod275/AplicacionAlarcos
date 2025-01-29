package com.example.aplicacionalarcos.fragmentInfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.aplicacionalarcos.databinding.ActivityViewTabBinding
import com.google.android.material.tabs.TabLayoutMediator

class ViewTab : AppCompatActivity() {
    private lateinit var binding: ActivityViewTabBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewTabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.root.id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar ViewPager2 con el Adapter
        val adapter = ViewPager(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Configurar TabLayout con ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "INFORMACION"
                1 -> "MANUEL"
                2 -> "VICTOR"
                else -> null
            }
        }.attach()
    }
}
