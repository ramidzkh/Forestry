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
package forestry.farming.blocks;

import javax.annotation.Nullable;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import net.minecraftforge.common.ToolType;

import forestry.core.blocks.BlockStructure;
import forestry.farming.tiles.TileFarmControl;
import forestry.farming.tiles.TileFarmGearbox;
import forestry.farming.tiles.TileFarmHatch;
import forestry.farming.tiles.TileFarmPlain;
import forestry.farming.tiles.TileFarmValve;

public class BlockFarm extends BlockStructure {

	private final EnumFarmBlockType type;
	private final EnumFarmMaterial farmMaterial;

	public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);

	public enum State implements IStringSerializable {
		PLAIN, BAND;

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	public BlockFarm(EnumFarmBlockType type, EnumFarmMaterial farmMaterial) {
		super(Block.Properties.of(Material.STONE)
				.strength(1.0f)
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(0));
		this.type = type;
		this.farmMaterial = farmMaterial;
		registerDefaultState(this.getStateDefinition().any().setValue(STATE, State.PLAIN));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(STATE);
	}

	@Override
	public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> list) {
		super.fillItemCategory(tab, list);
	}

	public EnumFarmBlockType getType() {
		return type;
	}

	public EnumFarmMaterial getFarmMaterial() {
		return farmMaterial;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		switch (type) {
			case GEARBOX:
				return new TileFarmGearbox();
			case HATCH:
				return new TileFarmHatch();
			case VALVE:
				return new TileFarmValve();
			case CONTROL:
				return new TileFarmControl();
			default:
				return new TileFarmPlain();
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
		return getType() == EnumFarmBlockType.CONTROL;
	}
}
