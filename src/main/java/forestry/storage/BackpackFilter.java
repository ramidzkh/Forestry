package forestry.storage;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import forestry.api.storage.IBackpackFilterConfigurable;

public class BackpackFilter implements IBackpackFilterConfigurable {

	private final List<Ingredient> accept = new ArrayList<>();
	private final List<Ingredient> reject = new ArrayList<>();

	@Override
	public void accept(Ingredient ingredient) {
		accept.add(ingredient);
	}

	@Override
	public void reject(Ingredient ingredient) {
		reject.add(ingredient);
	}

	@Override
	public void clear() {
		accept.clear();
		reject.clear();
	}

	@Override
	public boolean test(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return false;
		}

		// I think that the backpack denies anything except what is allowed, but from what is allowed you can say
		// what will be rejected (like an override)
		if (accept.stream().anyMatch(ingredient -> ingredient.test(itemStack))) {
			return reject.stream().noneMatch(ingredient -> ingredient.test(itemStack));
		} else {
			return false;
		}
	}
}
