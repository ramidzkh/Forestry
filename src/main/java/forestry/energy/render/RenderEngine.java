/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.energy.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import com.mojang.blaze3d.systems.RenderSystem;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.render.ForestryResource;
import forestry.core.render.IForestryRenderer;
import forestry.core.render.RenderHelper;
import forestry.core.tiles.TemperatureState;
import forestry.energy.tiles.TileEngine;

public class RenderEngine implements IForestryRenderer<TileEngine> {
	private final ModelRenderer boiler;
	private final ModelRenderer trunk;
	private final ModelRenderer piston;
	private final ModelRenderer extension;

	private enum Textures {

		BASE, PISTON, EXTENSION, TRUNK_HIGHEST, TRUNK_HIGHER, TRUNK_HIGH, TRUNK_MEDIUM, TRUNK_LOW
	}

	private ResourceLocation[] textures;
	private static final float[] angleMap = new float[6];

	static {
		angleMap[Direction.EAST.ordinal()] = (float) -Math.PI / 2;
		angleMap[Direction.WEST.ordinal()] = (float) Math.PI / 2;
		angleMap[Direction.UP.ordinal()] = 0;
		angleMap[Direction.DOWN.ordinal()] = (float) Math.PI;
		angleMap[Direction.SOUTH.ordinal()] = (float) Math.PI / 2;
		angleMap[Direction.NORTH.ordinal()] = (float) -Math.PI / 2;
	}

	public RenderEngine(String baseTexture) {
		int textureWidth = 64;
		int textureHeight = 32;
		boiler = new ModelRenderer(textureWidth, textureHeight, 0, 0);
		boiler.addBox(-8F, -8F, -8F, 16, 6, 16);
		boiler.x = 8;
		boiler.y = 8;
		boiler.z = 8;

		trunk = new ModelRenderer(textureWidth, textureHeight, 0, 0);
		trunk.addBox(-4F, -4F, -4F, 8, 12, 8);
		trunk.x = 8F;
		trunk.y = 8F;
		trunk.z = 8F;

		piston = new ModelRenderer(textureWidth, textureHeight, 0, 0);
		piston.addBox(-6F, -2, -6F, 12, 4, 12);
		piston.x = 8F;
		piston.y = 8F;
		piston.z = 8F;

		extension = new ModelRenderer(textureWidth, textureHeight, 0, 0);
		extension.addBox(-5F, -3, -5F, 10, 2, 10);
		extension.x = 8F;
		extension.y = 8F;
		extension.z = 8F;

		textures = new ResourceLocation[]{
				new ForestryResource(baseTexture + "base.png"),
				new ForestryResource(baseTexture + "piston.png"),
				new ForestryResource(baseTexture + "extension.png"),
				new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_highest.png"),
				new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_higher.png"),
				new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_high.png"),
				new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_medium.png"),
				new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_low.png"),};
	}

	@Override
	public void renderTile(TileEngine tile, RenderHelper helper) {
		World worldObj = tile.getWorldObj();
		BlockState blockState = worldObj.getBlockState(tile.getBlockPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.getValue(BlockBase.FACING);
			render(tile.getTemperatureState(), tile.progress, facing, helper);
		}
	}

	@Override
	public void renderItem(ItemStack stack, RenderHelper helper) {
		render(TemperatureState.COOL, 0.25F, Direction.UP, helper);
	}

	private void render(TemperatureState state, float progress, Direction orientation, RenderHelper helper) {
		RenderSystem.color3f(1f, 1f, 1f);

		float step;

		if (progress > 0.5) {
			step = 5.99F - (progress - 0.5F) * 2F * 5.99F;
		} else {
			step = progress * 2F * 5.99F;
		}

		float tfactor = step / 16;

		Vector3f rotation = new Vector3f(0, 0, 0);
		float[] translate = {orientation.getStepX(), orientation.getStepY(), orientation.getStepZ()};

		switch (orientation) {
			case EAST:
			case WEST:
			case DOWN:
				rotation.setZ(angleMap[orientation.ordinal()]);
				break;
			case SOUTH:
			case NORTH:
			default:
				rotation.setX(angleMap[orientation.ordinal()]);
				break;
		}

		helper.setRotation(rotation);
		helper.renderModel(textures[Textures.BASE.ordinal()], boiler);

		helper.push();

		helper.translate(translate[0] * tfactor, translate[1] * tfactor, translate[2] * tfactor);
		helper.renderModel(textures[Textures.PISTON.ordinal()], piston);
		helper.translate(-translate[0] * tfactor, -translate[1] * tfactor, -translate[2] * tfactor);

		ResourceLocation texture;

		switch (state) {
			case OVERHEATING:
				texture = textures[Textures.TRUNK_HIGHEST.ordinal()];
				break;
			case RUNNING_HOT:
				texture = textures[Textures.TRUNK_HIGHER.ordinal()];
				break;
			case OPERATING_TEMPERATURE:
				texture = textures[Textures.TRUNK_HIGH.ordinal()];
				break;
			case WARMED_UP:
				texture = textures[Textures.TRUNK_MEDIUM.ordinal()];
				break;
			case COOL:
			default:
				texture = textures[Textures.TRUNK_LOW.ordinal()];
				break;

		}
		helper.renderModel(texture, trunk);

		float chamberf = 2F / 16F;

		if (step > 0) {
			for (int i = 0; i <= step + 2; i += 2) {
				helper.renderModel(textures[Textures.EXTENSION.ordinal()], extension);
				helper.translate(translate[0] * chamberf, translate[1] * chamberf, translate[2] * chamberf);
			}
		}
		helper.pop();
	}
}
