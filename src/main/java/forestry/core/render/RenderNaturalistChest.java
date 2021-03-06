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
package forestry.core.render;

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
import forestry.core.tiles.TileNaturalistChest;

public class RenderNaturalistChest implements IForestryRenderer<TileNaturalistChest> {

	private final ModelRenderer lid;
	private final ModelRenderer base;
	private final ModelRenderer lock;
	private final ResourceLocation texture;

	public RenderNaturalistChest(String textureName) {
		this.base = new ModelRenderer(64, 64, 0, 19);
		this.base.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		this.lid = new ModelRenderer(64, 64, 0, 0);
		this.lid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.lid.y = 9.0F;
		this.lid.z = 1.0F;
		this.lock = new ModelRenderer(64, 64, 0, 0);
		this.lock.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.lock.y = 8.0F;
		texture = new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/" + textureName + ".png");
	}

	@Override
	public void renderTile(TileNaturalistChest tile, RenderHelper helper) {
		World worldObj = tile.getWorldObj();
		BlockState blockState = worldObj.getBlockState(tile.getBlockPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.getValue(BlockBase.FACING);
			render(facing, tile.prevLidAngle, tile.lidAngle, helper, helper.partialTicks);
		}
	}

	@Override
	public void renderItem(ItemStack stack, RenderHelper helper) {
		render(Direction.SOUTH, 0, 0, helper, helper.partialTicks);
	}

	public void render(Direction orientation, float prevLidAngle, float lidAngle, RenderHelper helper, float partialTick) {
		helper.push();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		helper.translate(0.5D, 0.5D, 0.5D);

		helper.rotate(Vector3f.YP.rotationDegrees(-orientation.toYRot()));
		helper.translate(-0.5D, -0.5D, -0.5D);

		float angle = prevLidAngle + (lidAngle - prevLidAngle) * partialTick;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		float rotation = -(angle * (float) Math.PI / 2.0F);
		helper.renderModel(texture, new Vector3f(rotation, 0.0F, 0.0F), lid, lock);
		helper.renderModel(texture, base);

		helper.pop();
	}
}
