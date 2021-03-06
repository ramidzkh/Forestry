package forestry.lepidopterology.features;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.config.Constants;
import forestry.lepidopterology.worldgen.CocoonDecorator;

public class LepidopterologyFeatures {
	public static final Feature<NoFeatureConfig> COCOON_DECORATOR = new CocoonDecorator();

	public static final ConfiguredFeature<?, ?> COCOON_DECORATOR_CONF = COCOON_DECORATOR.configured(NoFeatureConfig.INSTANCE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));

	public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		IForgeRegistry<Feature<?>> registry = event.getRegistry();

		registry.register(COCOON_DECORATOR.setRegistryName("cocoon_decorator"));

		Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Constants.MOD_ID, "cocoon_decorator"), COCOON_DECORATOR_CONF);
	}

	public static void onBiomeLoad(BiomeLoadingEvent event) {
		event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, COCOON_DECORATOR_CONF);
	}
}
