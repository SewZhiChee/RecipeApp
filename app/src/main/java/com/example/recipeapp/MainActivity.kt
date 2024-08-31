package com.example.recipeapp

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeapp.storage.Recipe
import com.example.recipeapp.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RecipeAdapter
    private val recipes = mutableListOf<Recipe>()
    private var filteredRecipes = mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load recipes from JSON
        loadRecipesFromJson()

        // Initialize the adapter
        adapter = RecipeAdapter(filteredRecipes) { recipe ->
            val intent = Intent(this, RecipeInfoActivity::class.java).apply {
                putExtra("recipe_name", recipe.name)
                putExtra("recipe_type", recipe.type)
                putExtra("recipe_image", recipe.image)
                putStringArrayListExtra("recipe_ingredients", ArrayList(recipe.ingredients))
                putStringArrayListExtra("recipe_steps", ArrayList(recipe.steps))
            }
            startActivity(intent)
        }

        // Set up RecyclerView
        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.recipeRecyclerView.adapter = adapter

        val types = listOf("All", "Main Dish", "Soup", "Dessert")
        val spinnerAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, types)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typeSpinner.adapter = spinnerAdapter

        binding.filterButton.setOnClickListener {
            val selectedType = binding.typeSpinner.selectedItem.toString()
            filterRecipes(selectedType)
        }

        filterRecipes("All")
    }

    private fun loadRecipesFromJson() {
        try {
            val inputStream = assets.open("recipetypes.json")
            val reader = InputStreamReader(inputStream)
            val jsonObject = JSONObject(reader.readText())
            val jsonArray = jsonObject.getJSONArray("recipes")
            val recipesList = mutableListOf<Recipe>()

            for (i in 0 until jsonArray.length()) {
                val recipeObject = jsonArray.getJSONObject(i)
                val recipe = Recipe(
                    id = recipeObject.getInt("id"),
                    name = recipeObject.getString("name"),
                    type = recipeObject.getString("type"),
                    image = recipeObject.getString("image"),
                    ingredients = recipeObject.getJSONArray("ingredients").let { jsonArr ->
                        List(jsonArr.length()) { jsonArr.getString(it) }
                    },
                    steps = recipeObject.getJSONArray("steps").let { jsonArr ->
                        List(jsonArr.length()) { jsonArr.getString(it) }
                    }
                )
                recipesList.add(recipe)
            }
            recipes.addAll(recipesList)
            filteredRecipes.addAll(recipesList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun filterRecipes(type: String) {
        filteredRecipes.clear()
        if (type == "All") {
            filteredRecipes.addAll(recipes)
        } else {
            filteredRecipes.addAll(recipes.filter { it.type == type })
        }
        adapter.notifyDataSetChanged()
    }
}