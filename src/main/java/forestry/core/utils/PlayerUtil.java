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
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.FakePlayerFactory;

public abstract class PlayerUtil {

	//TODO: use null everywhere instead of an emptyUUID
	private static final UUID emptyUUID = new UUID(0, 0);

	public static boolean isSameGameProfile(GameProfile player1, GameProfile player2) {
		UUID id1 = player1.getId();
		UUID id2 = player2.getId();
		if (id1 != null && id2 != null && !id1.equals(emptyUUID) && !id2.equals(emptyUUID)) {
			return id1.equals(id2);
		}

		return player1.getName() != null && player1.getName().equals(player2.getName());
	}

	public static String getOwnerName(@Nullable GameProfile profile) {
		if (profile == null) {
			return Translator.translateToLocal("for.gui.derelict");
		} else {
			return profile.getName();
		}
	}

	public static boolean actOnServer(PlayerEntity player, Consumer<ServerPlayerEntity> action) {
		if (player.level.isClientSide || !(player instanceof ServerPlayerEntity)) {
			return false;
		}
		action.accept(asServer(player));
		return true;
	}

	public static ClientPlayerEntity asClient(PlayerEntity player) {
		if (!(player instanceof ClientPlayerEntity)) {
			throw new IllegalStateException("Failed to cast player to its client version.");
		}
		return (ClientPlayerEntity) player;
	}

	public static ServerPlayerEntity asServer(PlayerEntity player) {
		if (!(player instanceof ServerPlayerEntity)) {
			throw new IllegalStateException("Failed to cast player to its server version.");
		}
		return (ServerPlayerEntity) player;
	}

	/**
	 * Get a player for a given World and GameProfile.
	 * If they are not in the World, returns a FakePlayer.
	 * Do not store references to the return value, to prevent worlds staying in memory.
	 */
	@Nullable
	public static PlayerEntity getPlayer(World world, @Nullable GameProfile profile) {
		if (profile == null || profile.getName() == null) {
			if (world instanceof ServerWorld) {
				return FakePlayerFactory.getMinecraft((ServerWorld) world);
			} else {
				return null;
			}
		}

		PlayerEntity player = world.getPlayerByUUID(profile.getId());
		if (player == null && world instanceof ServerWorld) {
			player = FakePlayerFactory.get((ServerWorld) world, profile);
		}
		return player;
	}

	/**
	 * Get a fake player for a given World and GameProfile.
	 * Do not store references to the return value, to prevent worlds staying in memory.
	 */
	@Nullable
	public static PlayerEntity getFakePlayer(World world, @Nullable GameProfile profile) {
		if (profile == null || profile.getName() == null) {
			if (world instanceof ServerWorld) {
				return FakePlayerFactory.getMinecraft((ServerWorld) world);
			} else {
				return null;
			}
		}

		if (world instanceof ServerWorld) {
			return FakePlayerFactory.get((ServerWorld) world, profile);
		}
		return null;
	}
}
