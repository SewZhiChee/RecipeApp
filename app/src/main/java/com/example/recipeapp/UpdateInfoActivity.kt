package com.example.recipeapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.databinding.UpdateInfoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class UpdateInfoActivity : AppCompatActivity() {
    private lateinit var binding: UpdateInfoBinding
    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = UpdateInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the passed data
        recipeId = intent.getIntExtra("recipe_id", -1)
        val recipeIngredients = intent.getStringArrayListExtra("recipe_ingredients")
        val recipeSteps = intent.getStringArrayListExtra("recipe_steps")

        // Populate the fields with existing data
        binding.ingredientsTextMultiLine.setText(recipeIngredients?.joinToString("\n"))
        binding.stepTextMultiLine.setText(recipeSteps?.joinToString("\n"))

        binding.confirmButton.setOnClickListener {
            updateRecipe()
        }
    }

    private fun updateRecipe() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val file = File(filesDir, "recipetypes.json")
                if (file.exists()) {
                    val jsonStr = file.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(jsonStr)
                    val jsonArray = jsonObject.getJSONArray("recipes")

                    // Find the recipe by ID and update it
                    for (i in 0 until jsonArray.length()) {
                        val recipe = jsonArray.getJSONObject(i)
                        if (recipe.getInt("id") == recipeId) {
                            recipe.put("ingredients", JSONArray(binding.ingredientsTextMultiLine.text.toString().split("\n")))
                            recipe.put("steps", JSONArray(binding.stepTextMultiLine.text.toString().split("\n")))
                            break
                        }
                    }

                    // Save the updated JSON back to the file
                    saveJsonToFile(file, jsonObject)

                    runOnUiThread {
                        Toast.makeText(this@UpdateInfoActivity, "Recipe updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@UpdateInfoActivity, "Failed to update recipe", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun saveJsonToFile(file: File, jsonObject: JSONObject) {
        CoroutineScope(Dispatchers.IO).launch {
            file.writeText(jsonObject.toString())
        }
    }
}