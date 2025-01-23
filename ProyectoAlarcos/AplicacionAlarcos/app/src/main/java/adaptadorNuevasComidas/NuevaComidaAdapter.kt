package adaptadorNuevasComidas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionalarcos.R

import modelosNuevasComidas.Ingrediente


class NuevaComidaAdapter(private val ingredientes: List<Ingrediente>) : RecyclerView.Adapter<NuevaComidaAdapter.IngredienteViewHolder>() {
    inner class IngredienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvValorEnergetico: TextView = itemView.findViewById(R.id.tvValorEnergetico)
        val tvGrasas: TextView = itemView.findViewById(R.id.tvGrasas)
        val tvCarbohidratos: TextView = itemView.findViewById(R.id.tvCarbohidratos)
        val tvProteinas: TextView = itemView.findViewById(R.id.tvProteinas)
        val tvSal: TextView = itemView.findViewById(R.id.tvSal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredienteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_nuevas_comidas, parent, false)
        return IngredienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredienteViewHolder, position: Int) {
        val ingrediente = ingredientes[position]
        holder.tvNombre.text = ingrediente.nombre
        holder.tvValorEnergetico.text = "Valor energético: ${ingrediente.valorEnergetico}"
        holder.tvGrasas.text = "Grasas: ${ingrediente.grasas}"
        holder.tvCarbohidratos.text = "Carbohidratos: ${ingrediente.carbohidratos}"
        holder.tvProteinas.text = "Proteínas: ${ingrediente.proteinas}"
        holder.tvSal.text = "Sal: ${ingrediente.sal}"
    }

    override fun getItemCount() = ingredientes.size
}