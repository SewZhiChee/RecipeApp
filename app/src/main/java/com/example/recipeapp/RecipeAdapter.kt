package com.example.recipeapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.storage.Recipe
import com.example.recipeapp.databinding.RecipeBinding

class RecipeAdapter(private val recipes: List<Recipe>, private val onViewButtonClick: (Recipe) -> Unit) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(private val binding: RecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            val recipeImage = getDrawableResource(recipe.image, itemView.context)
            if (recipeImage != 0) {
                binding.recipeImageView.setImageResource(recipeImage)
            } else {
                binding.recipeImageView.setImageResource(R.drawable.chickencurry)
            }

            binding.RecipeName.text = recipe.name
            binding.recipeType.text = recipe.type

            binding.viewbutton.setOnClickListener {
                onViewButtonClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = RecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    private fun getDrawableResource(imageName: String, context: Context): Int {
        return context.resources.getIdentifier(imageName, "drawable", context.packageName)
    }
}