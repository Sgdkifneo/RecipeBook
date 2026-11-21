package com.example.recipebook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(
    private val onRecipeClicked: (Recipe) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // The list starts with just the two flavor titles
    private val items = mutableListOf<ListItem>(
        ListItem.FlavorTitle(Flavor.SAVORY),
        ListItem.FlavorTitle(Flavor.SWEET)
    )

    // Box for "Savory" and "Sweet"
    class FlavorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.item_flavor_title)

        fun bind(item: ListItem.FlavorTitle) {
            title.text = when (item.flavor) {
                Flavor.SAVORY -> "Savory"
                Flavor.SWEET -> "Sweet"
            }
        }
    }

    // Box for one recipe
    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.item_recipe_title)

        fun bind(recipe: Recipe, onClick: (Recipe) -> Unit) {
            title.text = recipe.title
            // STEP 8: when the box is tapped, tell MainActivity
            itemView.setOnClickListener { onClick(recipe) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.FlavorTitle -> TYPE_FLAVOR
            is ListItem.RecipeItem -> TYPE_RECIPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_FLAVOR) {
            FlavorViewHolder(inflater.inflate(R.layout.item_flavor, parent, false))
        } else {
            RecipeViewHolder(inflater.inflate(R.layout.item_recipe, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListItem.FlavorTitle -> (holder as FlavorViewHolder).bind(item)
            is ListItem.RecipeItem -> (holder as RecipeViewHolder).bind(item.recipe, onRecipeClicked)
        }
    }

    override fun getItemCount(): Int = items.size

    // Savory recipes go just above the "Sweet" title, sweet recipes go at the end
    fun addRecipe(recipe: Recipe) {
        val index = if (recipe.flavor == Flavor.SAVORY) {
            items.indexOfFirst { it is ListItem.FlavorTitle && it.flavor == Flavor.SWEET }
        } else {
            items.size
        }
        items.add(index, ListItem.RecipeItem(recipe))
        notifyItemInserted(index)
    }

    // Used by the swipe: only recipes can be swiped, not the titles
    fun isRecipe(position: Int): Boolean = items[position] is ListItem.RecipeItem

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    companion object {
        private const val TYPE_FLAVOR = 0
        private const val TYPE_RECIPE = 1
    }
}