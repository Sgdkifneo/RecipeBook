package com.example.recipebook

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Keeps the screen away from the top bar, bottom bar and keyboard
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.main_recipes_list)
        val titleInput = findViewById<EditText>(R.id.main_recipe_title)
        val descriptionInput = findViewById<EditText>(R.id.main_recipe_description)

        // STEP 9: make the adapter and connect it to the list
        adapter = RecipeAdapter { recipe -> showRecipeDialog(recipe) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // STEP 9: the two buttons
        findViewById<Button>(R.id.main_add_savory_button).setOnClickListener {
            addRecipe(Flavor.SAVORY, titleInput, descriptionInput)
        }
        findViewById<Button>(R.id.main_add_sweet_button).setOnClickListener {
            addRecipe(Flavor.SWEET, titleInput, descriptionInput)
        }

        // STEP 10: swipe to delete
        val swipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            // Titles ("Savory", "Sweet") can't be swiped
            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.bindingAdapterPosition
                return if (position != RecyclerView.NO_POSITION && adapter.isRecipe(position)) {
                    super.getSwipeDirs(recyclerView, viewHolder)
                } else {
                    0
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    adapter.removeAt(position)
                }
            }
        })
        swipeHelper.attachToRecyclerView(recyclerView)
    }

    // STEP 9: add a recipe, then clear the form
    private fun addRecipe(flavor: Flavor, titleInput: EditText, descriptionInput: EditText) {
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Please type a recipe title", Toast.LENGTH_SHORT).show()
            return
        }

        adapter.addRecipe(Recipe(title, description, flavor))
        titleInput.text.clear()
        descriptionInput.text.clear()
    }

    // STEP 8: the dialog that shows the description
    private fun showRecipeDialog(recipe: Recipe) {
        AlertDialog.Builder(this)
            .setTitle(recipe.title)
            .setMessage(recipe.description.ifEmpty { "No description" })
            .setPositiveButton("OK", null)
            .show()
    }
}