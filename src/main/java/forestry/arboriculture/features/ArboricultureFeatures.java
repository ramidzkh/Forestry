package forestry.arboriculture.features;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;

import forestry.arboriculture.worldgen.TreeDecorator;
import forestry.core.config.Constants;

public class ArboricultureFeatures {
	public static final Feature<NoFeatureConfig> TREE_DECORATOR = new TreeDecorator();

	public static final ConfiguredFeature<?, ?> TREE_DECORATOR_CONF = TREE_DECORATOR.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));

	public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		IForgeRegistry<Feature<?>> registry = event.getRegistry();

		registry.register(TREE_DECORATOR.setRegistryName("tree_decorator"));

		Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Constants.MOD_ID, "tree_decorator"), TREE_DECORATOR_CONF);
	}

	public static void onBiomeLoad(BiomeLoadingEvent event) {
		event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, TREE_DECORATOR_CONF);
	}
}
