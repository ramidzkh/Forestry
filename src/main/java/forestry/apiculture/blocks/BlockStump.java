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
package forestry.apiculture.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import forestry.apiculture.features.ApicultureBlocks;
import forestry.apiculture.tiles.TileCandle;
import forestry.core.config.Constants;
import forestry.modules.features.FeatureBlock;

public class BlockStump extends TorchBlock {

	public BlockStump() {
		super(Block.Properties.of(Material.DECORATION)
				.strength(0.0f)
				.sound(SoundType.WOOD), ParticleTypes.FLAME);
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit) {
		return useStump(ApicultureBlocks.CANDLE, state, worldIn, pos, playerIn, hand);
	}

	public static ActionResultType useStump(FeatureBlock<?, ?> featureBlock, BlockState oldState, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand) {
		ItemStack heldItem = playerIn.getItemInHand(hand);
		if (BlockCandle.lightingItems.contains(heldItem.getItem())) {
			BlockState activatedState = featureBlock.with(BlockCandle.STATE, BlockCandle.State.ON);
			if (activatedState.hasProperty(WallTorchBlock.FACING)) {
				activatedState = activatedState.setValue(WallTorchBlock.FACING, oldState.getValue(WallTorchBlock.FACING));
			}
			worldIn.setBlock(pos, activatedState, Constants.FLAG_BLOCK_SYNC);
			TileCandle candle = new TileCandle();
			candle.setColour(DyeColor.WHITE.getColorValue()); // default to white
			candle.setLit(true);
			worldIn.setBlockEntity(pos, candle);
			worldIn.playSound(playerIn, pos, heldItem.getItem() == Items.FLINT_AND_STEEL ? SoundEvents.FLINTANDSTEEL_USE : SoundEvents.FIRE_AMBIENT, SoundCategory.BLOCKS, 0.75F, worldIn.random.nextFloat() * 0.4F + 0.8F);
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		// Empty for remove flame particles
	}
}
