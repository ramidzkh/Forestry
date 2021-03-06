package forestry.book.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.IBookCategory;
import forestry.api.book.IForesterBook;
import forestry.book.gui.buttons.GuiButtonBookCategory;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;

@OnlyIn(Dist.CLIENT)
public class GuiForestryBookCategories extends GuiForesterBook {
	public static final Drawable LOGO = new Drawable(new ResourceLocation(Constants.MOD_ID, "textures/gui/almanac/logo.png"), 0, 0, 256, 58, 256, 58);

	public GuiForestryBookCategories(IForesterBook book) {
		super(book);
	}

	@Override
	public void init() {
		super.init();
		int x = 0;
		int y = 0;
		for (IBookCategory category : book.getCategories()) {
			if (category.getEntries().isEmpty()) {
				continue;
			}
			addButton(new GuiButtonBookCategory(guiLeft + LEFT_PAGE_START_X + x * 36, guiTop + 25 + y * 36, category, this::actionPerformed));
			x++;
			if (x == 3) {
				y++;
				x = 0;
			}
		}
	}

	@Override
	protected boolean hasButtons() {
		return false;
	}

	@Override
	protected void drawText(MatrixStack transform) {
		drawCenteredString(transform, font, new TranslationTextComponent("for.gui.book.about.title").withStyle(TextFormatting.UNDERLINE), guiLeft + RIGHT_PAGE_START_X + 52, guiTop + PAGE_START_Y, 0xD3D3D3);
		TextComponent about = new TranslationTextComponent("for.gui.book.about");
		font.drawWordWrap(about, guiLeft + RIGHT_PAGE_START_X, guiTop + LEFT_PAGE_START_Y, 108, 0);
		font.draw(transform, new TranslationTextComponent("for.gui.book.about.author"), guiLeft + RIGHT_PAGE_START_X, guiTop + LEFT_PAGE_START_Y + font.wordWrapHeight(about.getString(), 108), 0);
		LOGO.draw(transform, guiTop + LEFT_PAGE_START_Y + 110, 108, 24, guiLeft + RIGHT_PAGE_START_X);
	}

	@Override
	protected void actionPerformed(Button button) {
		if (button instanceof GuiButtonBookCategory) {
			GuiButtonBookCategory buttonCategory = (GuiButtonBookCategory) button;
			Minecraft.getInstance().setScreen(new GuiForestryBookEntries(book, buttonCategory.category));
		}
	}

	@Override
	public ITextComponent getTitle() {
		return new TranslationTextComponent("for.gui.book.categories");
	}
}
