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
package forestry.mail.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.ITradeStationInfo;
import forestry.core.config.SessionVars;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.Widget;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.render.ColourProperties;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;

public class GuiCatalogue extends GuiForestry<ContainerCatalogue> {

	private static final String boldUnderline = TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE;

	private Button buttonFilter;
	private Button buttonUse;

	private final List<ItemStackWidget> tradeInfoWidgets = new ArrayList<>();

	public GuiCatalogue(ContainerCatalogue container, PlayerInventory inv, ITextComponent title) {
		super(new ResourceLocation("textures/gui/book.png"), container, inv, title);
		this.imageWidth = 192;
		this.imageHeight = 192;

		buttonFilter = new Button(width / 2 - 44, topPos + 150, 42, 20, new TranslationTextComponent("for.gui.mail.filter.all"), b -> actionPerformed(4));
		buttonUse = new Button(width / 2, topPos + 150, 42, 20, new TranslationTextComponent("for.gui.mail.address.copy"), b -> actionPerformed(5));
	}

	@Override
	public void init() {
		super.init();

		buttons.clear();
		children.clear();

		Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);

		addButton(new Button(width / 2 + 44, topPos + 150, 12, 20, new StringTextComponent(">"), b -> actionPerformed(2)));
		addButton(new Button(width / 2 - 58, topPos + 150, 12, 20, new StringTextComponent("<"), b -> actionPerformed(3)));

		//TODO but these are set in the constructor??
		buttonFilter = new Button(width / 2 - 44, topPos + 150, 42, 20, new TranslationTextComponent("for.gui.mail.filter.all"), b -> actionPerformed(4));
		addButton(buttonFilter);

		buttonUse = new Button(width / 2, topPos + 150, 42, 20, new TranslationTextComponent("for.gui.mail.address.copy"), b -> actionPerformed(5));
		addButton(buttonUse);
	}

	@Override
	public void removed() {
		Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
		super.removed();
	}

	@Override
	protected void renderBg(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
		super.renderBg(transform, partialTicks, mouseY, mouseX);

		Minecraft.getInstance().font.draw(transform, String.format("%s / %s", container.getPageNumber(), container.getPageCount()), leftPos + imageWidth - 72, topPos + 12, ColourProperties.INSTANCE.get("gui.book"));

		clearTradeInfoWidgets();

		ITradeStationInfo tradeInfo = container.getTradeInfo();

		if (tradeInfo != null) {
			drawTradePreview(transform, tradeInfo, leftPos + 38, topPos + 30);
			buttonUse.visible = tradeInfo.getState().isOk();
		} else {
			drawNoTrade(leftPos + 38, topPos + 30);
			buttonUse.visible = false;
		}

		buttonFilter.setMessage(new TranslationTextComponent("for.gui.mail.filter." + container.getFilterIdent()));
	}

	private void drawNoTrade(int x, int y) {
		Minecraft.getInstance().font.drawWordWrap(new TranslationTextComponent("for.gui.mail.notrades"), x, y + 18, 119, ColourProperties.INSTANCE.get("gui.book"));
	}

	private void drawTradePreview(MatrixStack transform, ITradeStationInfo tradeInfo, int x, int y) {

		FontRenderer fontRenderer = Minecraft.getInstance().font;
		fontRenderer.draw(transform, boldUnderline + tradeInfo.getAddress().getName(), x, y, ColourProperties.INSTANCE.get("gui.book"));

		fontRenderer.draw(transform, String.format(Translator.translateToLocal("for.gui.mail.willtrade"), tradeInfo.getOwner().getName()), x, y + 18, ColourProperties.INSTANCE.get("gui.book"));

		addTradeInfoWidget(new ItemStackWidget(widgetManager, x - leftPos, y - topPos + 28, tradeInfo.getTradegood()));

		fontRenderer.draw(transform, Translator.translateToLocal("for.gui.mail.tradefor"), x, y + 46, ColourProperties.INSTANCE.get("gui.book"));

		for (int i = 0; i < tradeInfo.getRequired().size(); i++) {
			ItemStack itemStack = tradeInfo.getRequired().get(i);
			addTradeInfoWidget(new ItemStackWidget(widgetManager, x - leftPos + i * 18, y - topPos + 56, itemStack));
		}

		//TODO: Fix later
		if (tradeInfo.getState().isOk()) {
			fontRenderer.drawWordWrap(((IFormattableTextComponent) tradeInfo.getState().getDescription()).withStyle(TextFormatting.DARK_GREEN), x, y + 82, 119, ColourProperties.INSTANCE.get("gui.book"));
		} else {
			fontRenderer.drawWordWrap(((IFormattableTextComponent) tradeInfo.getState().getDescription()).withStyle(TextFormatting.DARK_RED), x, y + 82, 119, ColourProperties.INSTANCE.get("gui.book"));
		}
	}

	private void addTradeInfoWidget(ItemStackWidget widget) {
		tradeInfoWidgets.add(widget);
		widgetManager.add(widget);
	}

	private void clearTradeInfoWidgets() {
		for (Widget widget : tradeInfoWidgets) {
			widgetManager.remove(widget);
		}
		tradeInfoWidgets.clear();
	}

	protected void actionPerformed(int id) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		switch (id) {
			case 0:
				player.closeContainer();
				break;
			case 2: // next page
				NetworkUtil.sendToServer(new PacketGuiSelectRequest(0, 0));
				break;
			case 3: // previous page
				NetworkUtil.sendToServer(new PacketGuiSelectRequest(1, 0));
				break;
			case 4: // cycle filter
				NetworkUtil.sendToServer(new PacketGuiSelectRequest(2, 0));
				break;
			case 5:
				ITradeStationInfo info = container.getTradeInfo();
				if (info != null) {
					SessionVars.setStringVar("mail.letter.recipient", info.getAddress().getName());
					SessionVars.setStringVar("mail.letter.addressee", EnumAddressee.TRADER.toString());
				}
				player.closeContainer();
				break;
		}
	}

	@Override
	protected void addLedgers() {

	}
}
