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
package forestry.core.utils;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import net.minecraftforge.registries.ForgeRegistries;

public abstract class EntityUtil {
	@Nullable
	public static <T extends MobEntity> T spawnEntity(World world, EntityType<T> type, double x, double y, double z) {
		T entityLiving = createEntity(world, type);
		if (entityLiving == null) {
			return null;
		}
		return spawnEntity(world, entityLiving, x, y, z);
	}

	public static <T extends MobEntity> T spawnEntity(World world, T living, double x, double y, double z) {
		living.moveTo(x, y, z, MathHelper.wrapDegrees(world.random.nextFloat() * 360.0f), 0.0f);
		living.yHeadRot = living.yRot;
		living.yBodyRot = living.yRot;
		DifficultyInstance diff = world.getCurrentDifficultyAt(new BlockPos(x, y, z));
		//TODO - check SpawnReason
		living.finalizeSpawn(WorldUtils.asServer(world), diff, SpawnReason.MOB_SUMMONED, null, null);
		world.addFreshEntity(living);
		//TODO - right sound?
		living.playAmbientSound();
		return living;
	}

	@Nullable
	private static <T extends MobEntity> T createEntity(World world, EntityType<T> type) {
		ResourceLocation name = ForgeRegistries.ENTITIES.getKey(type);
		if (name == null) {
			return null;
		}
		return type.create(world);
	}
}
