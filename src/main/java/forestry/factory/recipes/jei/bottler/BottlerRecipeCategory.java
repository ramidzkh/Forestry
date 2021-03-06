package forestry.factory.recipes.jei.bottler;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.mojang.blaze3d.matrix.MatrixStack;

import forestry.core.config.Constants;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;

public class BottlerRecipeCategory extends ForestryRecipeCategory<BottlerRecipeWrapper> {
	private static final int inputFull = 0;
	private static final int outputEmpty = 1;
	private static final int inputEmpty = 2;
	private static final int outputFull = 3;
	private static final int tankIndex = 0;

	private final static ResourceLocation guiTexture = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/bottler.png");

	private final IDrawable icon;
	private final IDrawable slot;
	private final IDrawable tank;
	private final IDrawable arrowDown;
	private final IDrawable tankOverlay;

	public BottlerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createBlankDrawable(62, 60), "block.forestry.bottler");

		this.slot = guiHelper.getSlotDrawable();
		this.tank = guiHelper.createDrawable(guiTexture, 79, 13, 18, 60);
		this.arrowDown = guiHelper.createDrawable(guiTexture, 20, 25, 12, 8);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
		this.icon = guiHelper.createDrawableIngredient(new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.BOTTLER).block()));
	}

	@Override
	public ResourceLocation getUid() {
		return ForestryRecipeCategoryUid.BOTTLER;
	}

	@Override
	public Class<? extends BottlerRecipeWrapper> getRecipeClass() {
		return BottlerRecipeWrapper.class;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BottlerRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		if (recipeWrapper.fillRecipe) {
			guiItemStacks.init(inputEmpty, true, 44, 0);
			guiItemStacks.init(outputFull, false, 44, 42);
			guiFluidStacks.init(tankIndex, true, 23, 1, 16, 58, 10000, false, tankOverlay);
		} else {
			guiItemStacks.init(inputFull, true, 0, 0);
			guiItemStacks.init(outputEmpty, false, 0, 42);
			guiFluidStacks.init(tankIndex, false, 23, 1, 16, 58, 10000, false, tankOverlay);
		}

		guiItemStacks.set(ingredients);
		guiFluidStacks.set(ingredients);
	}

	@Override
	public void draw(BottlerRecipeWrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		slot.draw(matrixStack, 0, 0);
		arrowDown.draw(matrixStack, 3, 26);
		slot.draw(matrixStack, 0, 42);

		tank.draw(matrixStack, 22, 0);

		slot.draw(matrixStack, 44, 0);
		arrowDown.draw(matrixStack, 47, 26);
		slot.draw(matrixStack, 44, 42);
	}
}
