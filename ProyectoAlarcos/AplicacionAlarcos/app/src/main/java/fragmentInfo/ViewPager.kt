package fragmentInfo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InformacionFragment()
            1 -> ManuelFragment()
            2 -> VictorFragment()
            else -> throw IllegalStateException("Posición inválida")
        }
    }
}
