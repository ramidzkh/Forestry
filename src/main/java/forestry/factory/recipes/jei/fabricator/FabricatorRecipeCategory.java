package forestry.factory.recipes.jei.fabricator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.factory.blocks.BlockTypeFactoryPlain;
import forestry.factory.features.FactoryBlocks;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;

public class FabricatorRecipeCategory extends ForestryRecipeCategory<FabricatorRecipeWrapper> {
	private static final int planSlot = 0;
	private static final int smeltingInputSlot = 1;
	private static final int craftOutputSlot = 2;
	private static final int craftInputSlot = 3;

	private static final int inputTank = 0;

	private final static ResourceLocation guiTexture = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/fabricator.png");
	private final ICraftingGridHelper craftingGridHelper;
	private final IDrawable icon;
	private final RecipeManager manager;

	public FabricatorRecipeCategory(IGuiHelper guiHelper, RecipeManager manager) {
		super(guiHelper.createDrawable(guiTexture, 20, 16, 136, 54), "block.forestry.fabricator");

		craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot);
		this.icon = guiHelper.createDrawableIngredient(new ItemStack(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.FABRICATOR).block()));
		this.manager = manager;
	}

	private static Map<Fluid, List<IFabricatorSmeltingRecipe>> getSmeltingInputs(RecipeManager manager) {
		Map<Fluid, List<IFabricatorSmeltingRecipe>> smeltingInputs = new HashMap<>();
		for (IFabricatorSmeltingRecipe smelting : RecipeManagers.fabricatorSmeltingManager.getRecipes(manager)) {
			Fluid fluid = smelting.getProduct().getFluid();
			if (!smeltingInputs.containsKey(fluid)) {
				smeltingInputs.put(fluid, new ArrayList<>());
			}

			smeltingInputs.get(fluid).add(smelting);
		}

		return smeltingInputs;
	}

	@Override
	public ResourceLocation getUid() {
		return ForestryRecipeCategoryUid.FABRICATOR;
	}

	@Override
	public Class<? extends FabricatorRecipeWrapper> getRecipeClass() {
		return FabricatorRecipeWrapper.class;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FabricatorRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(planSlot, true, 118, 0);

		guiItemStacks.init(smeltingInputSlot, true, 5, 4);

		guiItemStacks.init(craftOutputSlot, false, 118, 36);

		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 3; ++x) {
				int index = craftInputSlot + x + y * 3;
				guiItemStacks.init(index, true, 46 + x * 18, y * 18);
			}
		}

		guiFluidStacks.init(inputTank, true, 6, 32, 16, 16, 2000, false, null);

		IFabricatorRecipe recipe = recipeWrapper.getRecipe();

		Ingredient plan = recipe.getPlan();
		if (!plan.isEmpty()) {
			guiItemStacks.set(planSlot, plan.getItems()[0]);
		}

		List<ItemStack> smeltingInput = new ArrayList<>();
		Fluid recipeFluid = recipe.getLiquid().getFluid();
		for (IFabricatorSmeltingRecipe s : getSmeltingInputs(manager).get(recipeFluid)) {
			Optional<ItemStack> itemStack = Arrays.stream(s.getResource().getItems()).findFirst();
			itemStack.ifPresent(smeltingInput::add);
		}

		if (!smeltingInput.isEmpty()) {
			guiItemStacks.set(smeltingInputSlot, smeltingInput);
		}

		List<List<ItemStack>> itemOutputs = ingredients.getOutputs(VanillaTypes.ITEM);
		guiItemStacks.set(craftOutputSlot, itemOutputs.get(0));

		List<List<ItemStack>> itemStackInputs = ingredients.getInputs(VanillaTypes.ITEM);
		craftingGridHelper.setInputs(guiItemStacks, itemStackInputs, recipe.getCraftingGridRecipe().getWidth(), recipe.getCraftingGridRecipe().getHeight());

		List<List<FluidStack>> fluidInputs = ingredients.getInputs(VanillaTypes.FLUID);
		if (!fluidInputs.isEmpty()) {
			guiFluidStacks.set(inputTank, fluidInputs.get(0));
		}
	}
}
