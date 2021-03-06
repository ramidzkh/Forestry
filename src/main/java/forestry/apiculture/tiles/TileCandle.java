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
package forestry.apiculture.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.apiculture.features.ApicultureTiles;

public class TileCandle extends TileEntity {
	private int colour;
	private boolean lit;

	public TileCandle() {
		super(ApicultureTiles.CANDLE.tileType());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundNBT nbt = pkt.getTag();
		handleUpdateTag(getBlockState(), nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		return save(tag);
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.handleUpdateTag(state, tag);
		load(state, tag);
	}

	public void onPacketUpdate(int colour, boolean isLit) {
		this.colour = colour;
		this.lit = isLit;
	}

	@Override
	public void load(BlockState state, CompoundNBT tagRoot) {
		super.load(state, tagRoot);
		colour = tagRoot.getInt("colour");
		lit = tagRoot.getBoolean("lit");
	}

	@Override
	public CompoundNBT save(CompoundNBT tagRoot) {
		tagRoot = super.save(tagRoot);
		tagRoot.putInt("colour", this.colour);
		tagRoot.putBoolean("lit", this.lit);
		return tagRoot;
	}

	public boolean isLit() {
		return lit;
	}

	public void setLit(boolean lit) {
		this.lit = lit;
	}

	public int getColour() {
		return colour;
	}

	public void setColour(int value) {
		this.colour = value;
	}

	public void addColour(int colour2) {
		int[] myColour = fromIntColour(this.colour);
		int[] addColour = fromIntColour(colour2);
		this.colour = toIntColour((addColour[0] + myColour[0]) / 2, (addColour[1] + myColour[1]) / 2, (addColour[2] + myColour[2]) / 2);
	}

	private static int[] fromIntColour(int value) {
		int[] cs = new int[3];
		cs[0] = (value & 0xff0000) >> 16;
		cs[1] = (value & 0x00ff00) >> 8;
		cs[2] = value & 0x0000ff;
		return cs;
	}

	private static int toIntColour(int r, int g, int b) {
		return r << 16 | g << 8 | b;
	}
}
