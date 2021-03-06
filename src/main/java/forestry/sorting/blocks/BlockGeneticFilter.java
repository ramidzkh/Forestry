package forestry.sorting.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.minecraftforge.fml.network.NetworkHooks;

import forestry.core.blocks.BlockForestry;
import forestry.core.tiles.TileUtil;
import forestry.sorting.tiles.TileGeneticFilter;

public class BlockGeneticFilter extends BlockForestry {
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");

	private static final AxisAlignedBB BOX_CENTER = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB BOX_DOWN = new AxisAlignedBB(0.25, 0, 0.25, 0.75, 0.3125, 0.75);
	private static final AxisAlignedBB BOX_UP = new AxisAlignedBB(0.25, 0.6875, 0.25, 0.75, 1, 0.75);
	private static final AxisAlignedBB BOX_NORTH = new AxisAlignedBB(0.25, 0.25, 0, 0.75, 0.75, 0.3125);
	private static final AxisAlignedBB BOX_SOUTH = new AxisAlignedBB(0.25, 0.25, 0.6875, 0.75, 0.75, 1);
	private static final AxisAlignedBB BOX_WEST = new AxisAlignedBB(0, 0.25, 0.25, 0.3125, 0.75, 0.75);
	private static final AxisAlignedBB BOX_EAST = new AxisAlignedBB(0.6875, 0.25, 0.25, 1, 0.75, 0.75);
	private static final AxisAlignedBB[] BOX_FACES = {BOX_DOWN, BOX_UP, BOX_NORTH, BOX_SOUTH, BOX_WEST, BOX_EAST};

	public BlockGeneticFilter() {
		super(Block.Properties.of(Material.WOOD)
						.strength(0.25f, 3.0f)
						.dynamicShape()
						.noOcclusion(),
				false
		);
		this.registerDefaultState(this.getStateDefinition().any()
				.setValue(NORTH, false)
				.setValue(EAST, false)
				.setValue(SOUTH, false)
				.setValue(WEST, false)
				.setValue(UP, false)
				.setValue(DOWN, false));
	}

	public BlockState updateShape(BlockState state, Direction direction, BlockState changedState, IWorld world, BlockPos pos, BlockPos changedPos) {
		TileGeneticFilter geneticFilter = TileUtil.getTile(world, pos, TileGeneticFilter.class);
		if (geneticFilter == null) {
			return defaultBlockState();
		}
		return state.setValue(NORTH, geneticFilter.isConnected(Direction.NORTH))
				.setValue(EAST, geneticFilter.isConnected(Direction.EAST))
				.setValue(SOUTH, geneticFilter.isConnected(Direction.SOUTH))
				.setValue(WEST, geneticFilter.isConnected(Direction.WEST))
				.setValue(UP, geneticFilter.isConnected(Direction.UP))
				.setValue(DOWN, geneticFilter.isConnected(Direction.DOWN));
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult rayTraceResult) {
		TileGeneticFilter tile = TileUtil.getTile(worldIn, pos, TileGeneticFilter.class);
		if (tile != null) {
			if (TileUtil.isUsableByPlayer(playerIn, tile)) {
				if (!worldIn.isClientSide) {
					ServerPlayerEntity sPlayer = (ServerPlayerEntity) playerIn;
					NetworkHooks.openGui(sPlayer, tile, pos);
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	//TODO bounding boxes
	//	@Nullable
	//	@Override
	//	public RayTraceResult collisionRayTrace(BlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
	//		RayTraceResult result = rayTrace(pos, start, end, BOX_CENTER);
	//		if (result != null) {
	//			result.subHit = 0;
	//			return result;
	//		}
	//		TileGeneticFilter geneticFilter = TileUtil.getTile(worldIn, pos, TileGeneticFilter.class);
	//		if (geneticFilter != null) {
	//			for (Direction facing : Direction.VALUES) {
	//				if (geneticFilter.isConnected(facing)) {
	//					result = rayTrace(pos, start, end, BOX_FACES[facing.ordinal()]);
	//					if (result != null) {
	//						result.subHit = 1 + facing.ordinal();
	//						return result;
	//					}
	//				}
	//			}
	//		}
	//		return null;
	//	}
	//
	//	@Override
	//	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
	//		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_CENTER);
	//		TileGeneticFilter geneticFilter = TileUtil.getTile(worldIn, pos, TileGeneticFilter.class);
	//		if (geneticFilter != null) {
	//			for (Direction facing : Direction.VALUES) {
	//				if (geneticFilter.isConnected(facing)) {
	//					addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_FACES[facing.ordinal()]);
	//				}
	//			}
	//		}
	//	}
	//
	//	@Override
	//	@OnlyIn(Dist.CLIENT)
	//	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos) {
	//		RayTraceResult trace = Minecraft.getInstance().objectMouseOver;
	//		if (trace == null || trace.subHit < 0 || !pos.equals(trace.getBlockPos())) {
	//			return FULL_BLOCK_AABB.offset(pos);
	//		}
	//		AxisAlignedBB aabb = FULL_BLOCK_AABB;
	//		int sub = trace.subHit;
	//		if (sub == 0) {
	//			aabb = BOX_CENTER;
	//		} else if (sub < 1 + 6) {
	//			aabb = BOX_FACES[sub - 1];
	//		}
	//		return aabb.offset(pos);
	//	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	@Override
	@Nullable
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileGeneticFilter();
	}
}
