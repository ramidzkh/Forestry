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
package forestry.core.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.core.ItemGroupForestry;
import forestry.core.config.Config;
import forestry.core.items.definitions.EnumElectronTube;
import forestry.core.utils.ItemTooltipUtil;

public class ItemElectronTube extends ItemOverlay {

	private final EnumElectronTube type;

	public ItemElectronTube(EnumElectronTube type) {
		super(ItemGroupForestry.tabForestry, type);
		this.type = type;
	}

	public EnumElectronTube getType() {
		return type;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemstack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		Multimap<ICircuitLayout, ICircuit> circuits = getCircuits(itemstack);
		if (!circuits.isEmpty()) {
			if (Screen.hasShiftDown()) {
				for (ICircuitLayout circuitLayout : circuits.keys()) {
					String circuitLayoutName = circuitLayout.getUsage();
					list.add(new StringTextComponent(circuitLayoutName).withStyle(TextFormatting.WHITE, TextFormatting.UNDERLINE));
					for (ICircuit circuit : circuits.get(circuitLayout)) {
						circuit.addTooltip(list);
					}
				}
			} else {
				ItemTooltipUtil.addShiftInformation(itemstack, world, list, flag);
			}
		} else {
			list.add(new StringTextComponent("<")
					.append(new TranslationTextComponent("for.gui.noeffect")
							.append(">").withStyle(TextFormatting.GRAY)));
		}
	}

	@Override
	public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (this.allowdedIn(tab)) {
			if (Config.isDebug || !type.isSecret()) {
				ItemStack stack = new ItemStack(this);
				if (!getCircuits(stack).isEmpty()) {
					subItems.add(new ItemStack(this));
				}
			}
		}
	}

	private static Multimap<ICircuitLayout, ICircuit> getCircuits(ItemStack itemStack) {
		Multimap<ICircuitLayout, ICircuit> circuits = ArrayListMultimap.create();
		Collection<ICircuitLayout> allLayouts = ChipsetManager.circuitRegistry.getRegisteredLayouts().values();
		for (ICircuitLayout circuitLayout : allLayouts) {
			ICircuit circuit = ChipsetManager.solderManager.getCircuit(null, circuitLayout, itemStack);
			if (circuit != null) {
				circuits.put(circuitLayout, circuit);
			}
		}
		return circuits;
	}
}
