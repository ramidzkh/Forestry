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
package forestry.farming.logic.farmables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import forestry.farming.logic.crops.CropDestroy;

public class FarmableSapling implements IFarmable {
	protected final ItemStack germling;
	protected final Block saplingBlock;
	protected final ItemStack[] windfall;

	public FarmableSapling(final ItemStack germling, final ItemStack[] windfall) {
		this.germling = germling;
		this.windfall = windfall;
		this.saplingBlock = ItemStackUtil.getBlock(germling);
	}

	@Override
	public boolean plantSaplingAt(PlayerEntity player, ItemStack germling, World world, BlockPos pos) {
		ItemStack copy = germling.copy();
		player.setItemInHand(Hand.MAIN_HAND, copy);
		BlockRayTraceResult result = new BlockRayTraceResult(Vector3d.ZERO, Direction.UP, pos.below(), true);    //TODO isInside
		ActionResultType actionResult = copy.useOn(new ItemUseContext(player, Hand.MAIN_HAND, result));
		player.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
		if (actionResult.consumesAction()) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, Blocks.OAK_SAPLING.defaultBlockState());
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			return true;
		}
		return false;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos, BlockState blockState) {
		return blockState.getBlock() == this.saplingBlock;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos, BlockState blockState) {
		Block block = blockState.getBlock();
		if (!block.is(BlockTags.LOGS)) {
			return null;
		}

		return new CropDestroy(world, blockState, pos, null);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		//if (ignoreMetadata) {
		return ItemStack.isSame(germling, new ItemStack((itemstack.getItem())));
		/*}
		return ItemStack.isSame(germling, itemstack);*/
	}

	@Override
	public void addInformation(IFarmableInfo info) {
		NonNullList<ItemStack> germlings = NonNullList.create();
		//if (ignoreMetadata) {
		Item germlingItem = germling.getItem();
		ItemGroup tab = germlingItem.getItemCategory();
		if (tab != null) {
			germlingItem.fillItemCategory(tab, germlings);
		}
		//}
		if (germlings.isEmpty()) {
			germlings.add(germling);
		}
		info.addSeedlings(germlings);
		info.addProducts(windfall);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		for (ItemStack drop : windfall) {
			if (drop.sameItem(itemstack)) {
				return true;
			}
		}
		return false;
	}
}
