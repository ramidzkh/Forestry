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
package forestry.apiculture.genetics.alleles;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import net.minecraftforge.common.IPlantable;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;

import genetics.api.individual.IGenome;

public class AlleleEffectFertile extends AlleleEffectThrottled {

	private static final int MAX_BLOCK_FIND_TRIES = 5;

	public AlleleEffectFertile() {
		super("fertile", false, 6, true, false);
	}

	@Override
	public IEffectData doEffectThrottled(IGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorldObj();
		BlockPos housingCoordinates = housing.getCoordinates();
		Vector3i area = getModifiedArea(genome, housing);

		int blockX = getRandomOffset(world.random, housingCoordinates.getX(), area.getX());
		int blockZ = getRandomOffset(world.random, housingCoordinates.getZ(), area.getZ());
		int blockMaxY = housingCoordinates.getY() + area.getY() / 2 + 1;
		int blockMinY = housingCoordinates.getY() - area.getY() / 2 - 1;

		for (int attempt = 0; attempt < MAX_BLOCK_FIND_TRIES; ++attempt) {
			if (world.getChunkSource().getChunk(blockX >> 4, blockZ >> 4, false) != null) {
				if (tryTickColumn(world, blockX, blockZ, blockMaxY, blockMinY)) {
					break;
				}
				blockX = getRandomOffset(world.random, housingCoordinates.getX(), area.getX());
				blockZ = getRandomOffset(world.random, housingCoordinates.getZ(), area.getZ());
			}
		}

		return storedData;
	}

	private static int getRandomOffset(Random random, int centrePos, int offset) {
		return centrePos + random.nextInt(offset) - offset / 2;
	}

	private static boolean tryTickColumn(World world, int x, int z, int maxY, int minY) {
		for (int y = maxY; y >= minY; --y) {
			BlockState state = world.getBlockState(new BlockPos(x, y, z));
			Block block = state.getBlock();
			if (block.isRandomlyTicking(state) && (block instanceof IGrowable || block instanceof IPlantable)) {
				world.getBlockTicks().scheduleTick(new BlockPos(x, y, z), block, 5);
				return true;
			}
		}
		return false;
	}

}
