/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.circuits;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.registries.ForgeRegistryEntry;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.recipes.ISolderRecipe;

public class CircuitRecipe implements ISolderRecipe {

	private final ResourceLocation id;
	private final ICircuitLayout layout;
	private final ItemStack resource;
	private final ICircuit circuit;

	public CircuitRecipe(ResourceLocation id, ICircuitLayout layout, ItemStack resource, ICircuit circuit) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(layout, "Recipe layout cannot be null");
		Preconditions.checkNotNull(resource, "Recipe resource cannot be null");
		Preconditions.checkNotNull(circuit, "Recipe circuit cannot be null");

		this.id = id;
		this.resource = resource;
		this.layout = layout;
		this.circuit = circuit;
	}

	@Override
	public boolean matches(ICircuitLayout layout, ItemStack itemstack) {
		if (!this.layout.getUID().equals(layout.getUID())) {
			return false;
		}

		return itemstack.sameItem(resource);
	}

	@Override
	public ICircuitLayout getLayout() {
		return layout;
	}

	@Override
	public ItemStack getResource() {
		return resource;
	}

	@Override
	public ICircuit getCircuit() {
		return circuit;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CircuitRecipe> {

		@Override
		public CircuitRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout(JSONUtils.getAsString(json, "layout"));
			ICircuit circuit = ChipsetManager.circuitRegistry.getCircuit(JSONUtils.getAsString(json, "circuit"));
			ItemStack resource = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "resource"));

			return new CircuitRecipe(recipeId, layout, resource, circuit);
		}

		@Override
		public CircuitRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
			ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout(buffer.readUtf());
			ICircuit circuit = ChipsetManager.circuitRegistry.getCircuit(buffer.readUtf());
			ItemStack resource = buffer.readItem();

			return new CircuitRecipe(recipeId, layout, resource, circuit);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, CircuitRecipe recipe) {
			buffer.writeUtf(recipe.layout.getUID());
			buffer.writeUtf(recipe.circuit.getUID());
			buffer.writeItem(recipe.resource);
		}
	}
}
