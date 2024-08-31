package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.databinding.RecipeInfoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class RecipeInfoActivity : AppCompatActivity(){

    private lateinit var binding: RecipeInfoBinding
    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RecipeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeId = intent.getIntExtra("recipe_id", -1)
        val recipeName = intent.getStringExtra("recipe_name")
        val recipeImage = intent.getStringExtra("recipe_image")
        val recipeIngredients = intent.getStringArrayListExtra("recipe_ingredients")?: arrayListOf()
        val recipeSteps = intent.getStringArrayListExtra("recipe_steps")?: arrayListOf()

        binding.recipeNameTextView.text = recipeName
        val imageRes = resources.getIdentifier(recipeImage, "drawable", packageName)
        if (imageRes != 0) {
            binding.recipeImage.setImageResource(imageRes)
        }
        binding.ingredients.text = recipeIngredients?.joinToString("\n")
        binding.step.text = recipeSteps?.joinToString("\n")

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.updateButton.setOnClickListener {
            val intent = Intent(this, UpdateInfoActivity::class.java).apply {
                putExtra("recipe_id", recipeId)
                putExtra("recipe_name", recipeName)
                putExtra("recipe_image", recipeImage)
                putStringArrayListExtra("recipe_ingredients", recipeIngredients)
                putStringArrayListExtra("recipe_steps", recipeSteps)
            }
            startActivity(intent)
        }

        binding.deleteButton.setOnClickListener {
            deleteRecipe(recipeId)
        }
    }

    private fun deleteRecipe(recipeId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val file = File(filesDir, "recipetypes.json")
            if (file.exists()) {
                val jsonStr = file.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(jsonStr)
                val jsonArray = jsonObject.getJSONArray("recipes")

                val updatedArray = JSONArray()
                for (i in 0 until jsonArray.length()) {
                    val recipe = jsonArray.getJSONObject(i)
                    if (recipe.getInt("id") == recipeId) {
                        updatedArray.put(recipe)
                    }
                }

                jsonObject.put("recipes", updatedArray)

                // Save the updated JSON back to the file
                saveJsonToFile(file, jsonObject)

                // Notify user and finish activity
                runOnUiThread {
                    Toast.makeText(this@RecipeInfoActivity, "Recipe deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun saveJsonToFile(file: File, jsonObject: JSONObject) {
        CoroutineScope(Dispatchers.IO).launch {
            file.writeText(jsonObject.toString())
        }
    }
}