package forestry.apiculture.features;

import net.minecraft.entity.EntityClassification;

import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.entities.MinecartEntityApiary;
import forestry.apiculture.entities.MinecartEntityBeehouse;
import forestry.modules.features.FeatureEntityType;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class ApicultureEntities {

	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleApiculture.class);

	public static final FeatureEntityType<MinecartEntityApiary> APIARY_MINECART = REGISTRY
			.entity(MinecartEntityApiary::new, EntityClassification.MISC, "cart_apiary", (builder) -> builder.sized(0.98F, 0.7F));
	public static final FeatureEntityType<MinecartEntityBeehouse> BEE_HOUSE_MINECART = REGISTRY
			.entity(MinecartEntityBeehouse::new, EntityClassification.MISC, "cart_bee_house", (builder) -> builder.sized(0.98F, 0.7F));

	private ApicultureEntities() {
	}
}
