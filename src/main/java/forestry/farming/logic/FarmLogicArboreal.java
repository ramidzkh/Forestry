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
package forestry.farming.logic;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.PluginCore;

public class FarmLogicArboreal extends FarmLogicHomogeneous {
	public FarmLogicArboreal(ItemStack resource, IBlockState ground, Collection<IFarmable> germlings) {
		super(resource, ground, germlings);
	}

	public FarmLogicArboreal() {
		super(new ItemStack(Blocks.DIRT), PluginCore.blocks.humus.getDefaultState(), Farmables.farmables.get("farmArboreal"));
	}

	@Override
	public String getName() {
		return "Managed Arboretum";
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Item getItem() {
		return Item.getItemFromBlock(Blocks.SAPLING);
	}

	@Override
	public int getFertilizerConsumption() {
		return 10;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (10 * hydrationModifier);
	}

	@Override
	public Collection<ItemStack> collect(World world, IFarmHousing farmHousing) {
		Collection<ItemStack> products = produce;
		produce = collectEntityItems(world, farmHousing, true);
		return products;
	}

	private final Map<BlockPos, Integer> lastExtentsHarvest = new HashMap<>();

	@Override
	public Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent) {

		if (!lastExtentsHarvest.containsKey(pos)) {
			lastExtentsHarvest.put(pos, 0);
		}

		int lastExtent = lastExtentsHarvest.get(pos);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		BlockPos position = translateWithOffset(pos.up(), direction, lastExtent);
		Collection<ICrop> crops = harvestBlocks(world, position);
		lastExtent++;
		lastExtentsHarvest.put(pos, lastExtent);

		return crops;
	}

	private Collection<ICrop> harvestBlocks(World world, BlockPos position) {
		// Determine what type we want to harvest.
		IFarmable farmable = getFarmableForBlock(world, position, farmables);
		if (farmable == null) {
			return Collections.emptyList();
		}

		// get all crops of the same type that are connected to the first one
		Stack<BlockPos> candidates = new Stack<>();
		candidates.add(position);

		Set<BlockPos> seen = new HashSet<>();
		Stack<ICrop> crops = new Stack<>();

		while (!candidates.empty()) {
			BlockPos candidate = candidates.pop();
			processHarvestBlock(world, candidate, farmable, crops, candidates, seen);
		}

		return crops;
	}

	private static IFarmable getFarmableForBlock(World world, BlockPos position, Collection<IFarmable> farmables) {
		IBlockState blockState = world.getBlockState(position);
		for (IFarmable farmable : farmables) {
			ICrop crop = farmable.getCropAt(world, position, blockState);
			if (crop != null) {
				return farmable;
			}
		}
		return null;
	}

	private static void processHarvestBlock(World world, BlockPos position, IFarmable farmable, Stack<ICrop> crops, Stack<BlockPos> candidates, Set<BlockPos> seen) {
		for (BlockPos candidate : BlockPos.getAllInBox(position.add(-1, -1, -1), position.add(1, 1, 1))) {
			if (!seen.contains(candidate)) {
				seen.add(candidate);

				IBlockState blockState = world.getBlockState(candidate);
				ICrop crop = farmable.getCropAt(world, candidate, blockState);
				if (crop != null) {
					crops.push(crop);
					candidates.push(candidate);
				}
			}
		}
	}

	@Override
	protected boolean maintainGermlings(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);

			if (world.isAirBlock(position)) {
				BlockPos soilPosition = position.down();
				IBlockState soilState = world.getBlockState(soilPosition);
				if (isAcceptedSoil(soilState)) {
					return plantSapling(world, farmHousing, position);
				}
			}
		}
		return false;
	}

	private boolean plantSapling(World world, IFarmHousing farmHousing, BlockPos position) {
		Collections.shuffle(farmables);
		for (IFarmable candidate : farmables) {
			if (farmHousing.plantGermling(candidate, world, position)) {
				return true;
			}
		}

		return false;
	}

}
