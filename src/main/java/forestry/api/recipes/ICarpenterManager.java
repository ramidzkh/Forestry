/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;

/**
 * Provides an interface to the recipe manager of the carpenter.
 * <p>
 * The manager is initialized at the beginning of Forestry's BaseMod.load() cycle. Begin adding recipes in BaseMod.ModsLoaded() and this shouldn't be null even
 * if your mod loads before Forestry.
 * <p>
 * Accessible via {@link RecipeManagers}
 * <p>
 * Only shaped recipes can be added currently.
 *
 * @author SirSengir
 */
public interface ICarpenterManager extends ICraftingProvider<ICarpenterRecipe> {

	/**
	 * Add a shaped recipe to the carpenter.
	 *
	 * @param box       ItemStack of one item representing the required box (carton, crate) for this recipe. May be null.
	 * @param product   Crafting result.
	 * @param materials Materials needed in the crafting matrix. This gets passed directly to {@link ShapedRecipe}. Notation is the same.
	 */
	void addRecipe(ItemStack box, ItemStack product, Object... materials);

	/**
	 * Add a shaped recipe to the carpenter.
	 *
	 * @param packagingTime Number of work cycles required to craft the recipe once.
	 * @param box           ItemStack of one item representing the required box (carton, crate) for this recipe. May be empty.
	 * @param product       Crafting result.
	 * @param materials     Materials needed in the crafting matrix. This gets passed directly to {@link ShapedRecipe}. Notation is the same.
	 */
	void addRecipe(int packagingTime, ItemStack box, ItemStack product, Object... materials);

	/**
	 * Add a shaped recipe to the carpenter.
	 *
	 * @param packagingTime Number of work cycles required to craft the recipe once.
	 * @param liquid        Liquid required in carpenter's tank.
	 * @param box           ItemStack of one item representing the required box (carton, crate) for this recipe. May be empty.
	 * @param product       Crafting result.
	 * @param materials     Materials needed in the crafting matrix. This gets passed directly to {@link ShapedRecipe}. Notation is the same.
	 */
	void addRecipe(int packagingTime, @Nullable FluidStack liquid, ItemStack box, ItemStack product, Object... materials);

	/**
	 * Finds the matching recipe
	 *
	 * @param liquid    Present liquid
	 * @param item      Present item
	 * @param inventory Present inventory
	 * @param world		Current world
	 * @return An optional carpenter recipe if any matches
	 */
	Optional<ICarpenterRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, FluidStack liquid, ItemStack item, IInventory inventory, World world);

	boolean matches(@Nullable ICarpenterRecipe recipe, FluidStack resource, ItemStack item, IInventory craftingInventory, World world);

	boolean isBox(@Nullable RecipeManager recipeManager, ItemStack resource);

	Collection<ICarpenterRecipe> getRecipesWithOutput(@Nullable RecipeManager recipeManager, ItemStack output);

	Set<ResourceLocation> getRecipeFluids(@Nullable RecipeManager recipeManager);
}
