package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;

public class BlockForestrySlab extends SlabBlock implements IWoodTyped {

	private final boolean fireproof;
	private final IWoodType woodType;

	public BlockForestrySlab(BlockForestryPlank plank) {
		super(Block.Properties.copy(plank));
		this.fireproof = plank.isFireproof();
		this.woodType = plank.getWoodType();
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Override
	public IWoodType getWoodType() {
		return woodType;
	}

	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return fireproof ? 0 : 20;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return fireproof ? 0 : 5;
	}

	@Override
	public WoodBlockKind getBlockKind() {
		return WoodBlockKind.SLAB;
	}
}
