package forestry.apiculture.items;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import forestry.api.apiculture.ApicultureCapabilities;
import forestry.api.apiculture.hives.IHiveTile;
import forestry.api.core.ItemGroups;
import forestry.core.items.ItemForestry;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.TileUtil;

public class ItemSmoker extends ItemForestry {
	public ItemSmoker() {
		super((new Item.Properties())
				.stacksTo(1)
				.tab(ItemGroups.tabApiculture));
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (worldIn.isClientSide && isSelected && worldIn.random.nextInt(40) == 0) {
			addSmoke(stack, worldIn, entityIn, 1);
		}
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
		super.onUsingTick(stack, player, count);
		World world = player.level;
		addSmoke(stack, world, player, (count % 5) + 1);
	}

	private static HandSide getHandSide(ItemStack stack, Entity entity) {
		if (entity instanceof LivingEntity) {
			LivingEntity LivingEntity = (LivingEntity) entity;
			Hand activeHand = LivingEntity.getUsedItemHand();
			HandSide handSide = LivingEntity.getMainArm();
			if (activeHand == Hand.OFF_HAND) {
				// TODO: use EnumHandSide.opposite() when it's no longer client-only
				handSide = handSide == HandSide.LEFT ? HandSide.RIGHT : HandSide.LEFT;
			}
			return handSide;
		}
		return HandSide.RIGHT;
	}

	private static void addSmoke(ItemStack stack, World world, Entity entity, int distance) {
		if (distance <= 0) {
			return;
		}
		Vector3d look = entity.getLookAngle();
		HandSide handSide = getHandSide(stack, entity);

		Vector3d handOffset;
		if (handSide == HandSide.RIGHT) {
			handOffset = look.cross(new Vector3d(0, 1, 0));
		} else {
			handOffset = look.cross(new Vector3d(0, -1, 0));
		}

		Vector3d lookDistance = new Vector3d(look.x * distance, look.y * distance, look.z * distance);
		Vector3d scaledOffset = handOffset.scale(1.0 / distance);
		Vector3d smokePos = lookDistance.add(entity.position()).add(scaledOffset);

		if (world.isClientSide) {
			ParticleRender.addEntitySmokeFX(world, smokePos.x, smokePos.y + 1, smokePos.z);
		}

		BlockPos blockPos = new BlockPos(smokePos.x, smokePos.y + 1, smokePos.z);
		TileUtil.actOnTile(world, blockPos, IHiveTile.class, IHiveTile::calmBees);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		playerIn.startUsingItem(handIn);
		ItemStack itemStack = playerIn.getItemInHand(handIn);
		return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		TileUtil.actOnTile(context.getLevel(), context.getClickedPos(), IHiveTile.class, IHiveTile::calmBees);
		return super.onItemUseFirst(stack, context);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ICapabilityProvider() {

			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
				if (capability == ApicultureCapabilities.ARMOR_APIARIST) {
					return LazyOptional.of(capability::getDefaultInstance);
				}
				return LazyOptional.empty();
			}
		};
	}
}
