package forestry.apiculture.compat;

import java.util.Optional;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.apiculture.features.ApicultureItems;
import forestry.core.config.Constants;
import forestry.core.utils.JeiUtil;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

import genetics.api.GeneticHelper;
import genetics.api.individual.IIndividual;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class ApicultureJeiPlugin implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Constants.MOD_ID);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
		if (!ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			return;
		}

		ISubtypeInterpreter beeSubtypeInterpreter = itemStack -> {
			Optional<IIndividual> individual = GeneticHelper.getIndividual(itemStack);
			return individual.map(iIndividual -> iIndividual.getGenome().getPrimary().getBinomial()).orElse(ISubtypeInterpreter.NONE);
		};

		subtypeRegistry.registerSubtypeInterpreter(ApicultureItems.BEE_DRONE.item(), beeSubtypeInterpreter);
		subtypeRegistry.registerSubtypeInterpreter(ApicultureItems.BEE_PRINCESS.item(), beeSubtypeInterpreter);
		subtypeRegistry.registerSubtypeInterpreter(ApicultureItems.BEE_QUEEN.item(), beeSubtypeInterpreter);
		subtypeRegistry.registerSubtypeInterpreter(ApicultureItems.BEE_LARVAE.item(), beeSubtypeInterpreter);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		if (!ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			return;
		}

		JeiUtil.addDescription(registry, "frames", ApicultureItems.FRAME_IMPREGNATED.getItem(), ApicultureItems.FRAME_PROVEN.getItem(), ApicultureItems.FRAME_UNTREATED.getItem());

		JeiUtil.addDescription(registry, "apiarist.suit", ApicultureItems.APIARIST_BOOTS.getItem(), ApicultureItems.APIARIST_CHEST.getItem(), ApicultureItems.APIARIST_HELMET.getItem(), ApicultureItems.APIARIST_LEGS.getItem());

		JeiUtil.addDescription(registry, ApicultureItems.HABITAT_LOCATOR.getItem(), ApicultureItems.SCOOP.getItem(), ApicultureItems.IMPRINTER.getItem());
	}
}
