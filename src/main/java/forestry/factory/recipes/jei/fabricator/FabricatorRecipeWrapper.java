package forestry.factory.recipes.jei.fabricator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;

public class FabricatorRecipeWrapper extends ForestryRecipeWrapper<IFabricatorRecipe> {
	public FabricatorRecipeWrapper(IFabricatorRecipe recipe) {
		super(recipe);
	}

	@Override
	public void setIngredients(IIngredients ingredients) {
		IFabricatorRecipe recipe = getRecipe();

		NonNullList<Ingredient> itemIngredients = recipe.getCraftingGridRecipe().getIngredients();
		List<Ingredient> inputStacks = new ArrayList<>();
		inputStacks.addAll(itemIngredients);

		ingredients.setInputIngredients(inputStacks);

		ingredients.setInputs(VanillaTypes.FLUID, Collections.singletonList(getRecipe().getLiquid()));

		ingredients.setOutput(VanillaTypes.ITEM, recipe.getCraftingGridRecipe().getResultItem());
	}
}
