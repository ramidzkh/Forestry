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
package forestry.core.items;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;

/**
 * {@link net.minecraft.item.BlockItem} that gives it's nbt data to the {@link net.minecraft.tileentity.TileEntity}
 * of the placed block.
 * <p>
 * Used by the {@link forestry.worktable.tiles.TileWorktable} to save the memorized recipes
 */
public class ItemBlockNBT extends ItemBlockForestry<Block> {

	public ItemBlockNBT(Block block) {
		super(block);
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState blockState) {
		if (getBlock().hasTileEntity(blockState) && stack.hasTag()) {
			TileForestry tile = TileUtil.getTile(world, pos, TileForestry.class);
			if (tile != null) {
				tile.load(blockState, stack.getTag());
				tile.setPosition(pos);
			}
		}
		return super.updateCustomBlockEntityTag(pos, world, player, stack, blockState);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemstack, @Nullable World world, List<ITextComponent> info, ITooltipFlag advanced) {
		super.appendHoverText(itemstack, world, info, advanced);

		if (itemstack.getTag() != null) {
			info.add(new StringTextComponent("There are still some scribbles on this."));
		}
	}
}
