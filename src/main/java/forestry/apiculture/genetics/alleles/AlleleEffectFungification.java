package forestry.apiculture.genetics.alleles;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.EffectData;
import forestry.core.utils.VectUtil;

import genetics.api.individual.IGenome;

public class AlleleEffectFungification extends AlleleEffectThrottled {

	private static final int MAX_BLOCK_FIND_TRIES = 10;
	private static final int ENTITY_THROTTLE = 6;

	public AlleleEffectFungification() {
		super("mycophilic", true, 20, false, false);
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		if (storedData instanceof EffectData && ((EffectData) storedData).getIntSize() == 2) {
			return storedData;
		}

		return new EffectData(2, 0);
	}

	@Override
	public IEffectData doEffectThrottled(IGenome genome, IEffectData storedData, IBeeHousing housing) {

		doBlockEffect(genome, housing);

		int entityThrottle = storedData.getInteger(1);
		if (entityThrottle >= ENTITY_THROTTLE) {
			doEntityEffect(genome, housing);
			entityThrottle = 0;
		} else {
			++entityThrottle;
		}
		storedData.setInteger(1, entityThrottle);

		return storedData;
	}

	private void doBlockEffect(IGenome genome, IBeeHousing housing) {
		World world = housing.getWorldObj();
		BlockPos housingCoordinates = housing.getCoordinates();
		Vector3i area = getModifiedArea(genome, housing);
		Vector3i halfArea = new Vector3i(area.getX() / 2, area.getY() / 2, area.getZ() / 2);

		for (int attempt = 0; attempt < MAX_BLOCK_FIND_TRIES; ++attempt) {
			BlockPos pos = VectUtil.getRandomPositionInArea(world.random, area).subtract(halfArea).offset(housingCoordinates);
			if (world.hasChunkAt(pos)) {
				BlockState blockState = world.getBlockState(pos);

				if (convertToMycelium(world, blockState, pos)) {
					return;
				} else if (growGiantMushroom((ServerWorld) world, blockState, pos)) {
					return;
				}
			}
		}
	}

	private static void doEntityEffect(IGenome genome, IBeeHousing housing) {
		List<CowEntity> cows = getEntitiesInRange(genome, housing, CowEntity.class);
		for (CowEntity cow : cows) {
			if (convertCowToMooshroom(cow)) {
				return;
			}
		}
	}

	private static boolean convertToMycelium(World world, BlockState blockState, BlockPos pos) {
		Block block = blockState.getBlock();
		if (block == Blocks.GRASS || block == Blocks.DIRT && world.canSeeSkyFromBelowWater(pos)) {
			world.setBlockAndUpdate(pos, Blocks.MYCELIUM.defaultBlockState());
			return true;
		}
		return false;
	}

	private static boolean growGiantMushroom(ServerWorld world, BlockState blockState, BlockPos pos) {
		Block block = blockState.getBlock();
		if (block instanceof MushroomBlock) {
			MushroomBlock mushroom = (MushroomBlock) block;
			mushroom.growMushroom(world, pos, blockState, world.random);
			return true;
		}
		return false;
	}

	private static boolean convertCowToMooshroom(CowEntity cow) {
		if (cow instanceof MooshroomEntity) {
			return false;
		}
		World world = cow.level;
		cow.remove();
		MooshroomEntity mooshroom = new MooshroomEntity(EntityType.MOOSHROOM, world);
		mooshroom.moveTo(cow.getX(), cow.getY(), cow.getZ(), cow.yRot, cow.xRot);
		mooshroom.setHealth(cow.getHealth());
		mooshroom.yBodyRot = cow.yBodyRot;
		world.addFreshEntity(mooshroom);
		world.addParticle(ParticleTypes.EXPLOSION, cow.getX(), cow.getY() + cow.getBbHeight() / 2.0F, cow.getZ(), 0.0D, 0.0D, 0.0D);
		return true;
	}
}
