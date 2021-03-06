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
package forestry.core.entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import net.minecraftforge.fml.network.NetworkHooks;

import forestry.core.tiles.ITitled;
import forestry.core.utils.PlayerUtil;

//TODO - check nothing missing from MinecartEntity now that this extends AbstractMinecartEntity
public abstract class MinecartEntityForestry extends AbstractMinecartEntity implements ITitled {

	public MinecartEntityForestry(EntityType<? extends MinecartEntityForestry> type, World world) {
		super(type, world);
		setCustomDisplay(true);
	}

	public MinecartEntityForestry(EntityType<?> type, World world, double posX, double posY, double posZ) {
		super(type, world, posX, posY, posZ);
		setCustomDisplay(true);
	}

	// Needed to spawn the entity on the client
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public ActionResultType interact(PlayerEntity player, Hand hand) {
		ActionResultType ret = super.interact(player, hand);
		if (ret.consumesAction()) {
			return ret;
		}
		PlayerUtil.actOnServer(player, this::openGui);
		return ActionResultType.sidedSuccess(this.level.isClientSide);
	}

	protected abstract void openGui(ServerPlayerEntity player);

	/* MinecartEntity */
	@Override
	public boolean canBeRidden() {
		return false;
	}

	@Override
	public boolean isPoweredCart() {
		return false;
	}

	// cart contents
	@Override
	public abstract BlockState getDisplayBlockState();

	// cart itemStack
	@Override
	public abstract ItemStack getCartItem();

	@Override
	public void destroy(DamageSource damageSource) {
		super.destroy(damageSource);
		if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			Block block = getDisplayBlockState().getBlock();
			spawnAtLocation(new ItemStack(block), 0.0F);
		}
	}

	// fix cart contents rendering as black in the End dimension
	@Override
	public float getBrightness() {
		return 1.0f;
	}

	@Override
	public ITextComponent getName() {
		return new TranslationTextComponent(getUnlocalizedTitle());
	}

	/* ITitled */
	@Override
	public String getUnlocalizedTitle() {
		ItemStack cartItem = getCartItem();
		return cartItem.getDescriptionId();
	}

	@Override
	public Type getMinecartType() {
		return null;
	}
}
