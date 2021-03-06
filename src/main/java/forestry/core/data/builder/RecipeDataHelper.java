package forestry.core.data.builder;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;

import forestry.core.config.Constants;
import forestry.core.recipes.ModuleEnabledCondition;

/**
 * Useful when there is either a recipe, or it is disabled. Convenience from not having to provide an ID when building
 * <p>
 * also contains convenience method for module condition
 */
public class RecipeDataHelper {

	private final Consumer<IFinishedRecipe> consumer;

	public RecipeDataHelper(Consumer<IFinishedRecipe> consumer) {
		this.consumer = consumer;
	}

	public Consumer<IFinishedRecipe> getConsumer() {
		return consumer;
	}

	public void simpleConditionalRecipe(Consumer<Consumer<IFinishedRecipe>> recipe, ICondition... conditions) {
		simpleConditionalRecipe(recipe, null, conditions);
	}

	public void simpleConditionalRecipe(Consumer<Consumer<IFinishedRecipe>> recipe, @Nullable ResourceLocation id, ICondition... conditions) {
		ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
		for (ICondition condition : conditions) {
			builder.addCondition(condition);
		}

		Holder<IFinishedRecipe> finishedRecipeHolder = new Holder<>();
		recipe.accept(finishedRecipeHolder::set);

		IFinishedRecipe finishedRecipe = finishedRecipeHolder.get();
		builder.addRecipe(finishedRecipe);
		builder.build(consumer, id == null ? finishedRecipe.getId() : id);
	}

	public void moduleConditionRecipe(Consumer<Consumer<IFinishedRecipe>> recipe, @Nullable ResourceLocation id, String... moduleUIDs) {
		simpleConditionalRecipe(recipe, id, Arrays.stream(moduleUIDs).map(u -> new ModuleEnabledCondition(Constants.MOD_ID, u)).toArray(ICondition[]::new));
	}

	public void moduleConditionRecipe(Consumer<Consumer<IFinishedRecipe>> recipe, String... moduleUIDs) {
		moduleConditionRecipe(recipe, null, moduleUIDs);
	}
}
