package com.example.recipeapp.storage

data class Recipe(
    val id: Int,
    val name: String,
    val type: String,
    val image: String,
    var ingredients: List<String>,
    var steps: List<String>
)