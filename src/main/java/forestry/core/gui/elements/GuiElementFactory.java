package forestry.core.gui.elements;

import javax.annotation.Nullable;
import java.awt.Insets;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.api.genetics.gatgets.IDatabasePlugin;
import forestry.api.genetics.gatgets.IGeneticAnalyzer;
import forestry.api.genetics.gatgets.IGeneticAnalyzerProvider;
import forestry.core.config.Constants;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiConstants;
import forestry.core.gui.elements.layouts.ContainerElement;
import forestry.core.gui.elements.layouts.FlexLayout;
import forestry.core.render.ColourProperties;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.mutation.IMutation;

public class GuiElementFactory implements ISelectiveResourceReloadListener {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/database_mutation_screen.png");

	public Style dominantStyle = Style.EMPTY;
	public Style recessiveStyle = Style.EMPTY;
	public Style guiStyle = Style.EMPTY;
	public Style guiTitleStyle = Style.EMPTY;
	public Style databaseTitle = Style.EMPTY;
	public Style binomial = Style.EMPTY;

	/* Drawables */
	private static final Drawable QUESTION_MARK = new Drawable(TEXTURE, 78, 240, 16, 16);
	private static final Drawable DOWN_SYMBOL = new Drawable(TEXTURE, 0, 247, 15, 9);
	private static final Drawable UP_SYMBOL = new Drawable(TEXTURE, 15, 247, 15, 9);
	private static final Drawable BOOTH_SYMBOL = new Drawable(TEXTURE, 30, 247, 15, 9);
	private static final Drawable NONE_SYMBOL = new Drawable(TEXTURE, 45, 247, 15, 9);

	/* Instance */
	public static final GuiElementFactory INSTANCE = new GuiElementFactory();

	private GuiElementFactory() {
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
		dominantStyle = Style.EMPTY.withColor(Color.fromRgb(ColourProperties.INSTANCE.get("gui.beealyzer.dominant")));
		recessiveStyle = Style.EMPTY.withColor(Color.fromRgb(ColourProperties.INSTANCE.get("gui.beealyzer.recessive")));
		guiStyle = Style.EMPTY.withColor(Color.fromRgb(ColourProperties.INSTANCE.get("gui.screen")));
		guiTitleStyle = Style.EMPTY.withColor(Color.fromRgb(ColourProperties.INSTANCE.get("gui.title")));
		databaseTitle = Style.EMPTY.withColor(Color.fromRgb(0xcfb53b)).setUnderlined(true);
		binomial = Style.EMPTY.withColor(Color.fromRgb(ColourProperties.INSTANCE.get("gui.beealyzer.binomial")));
	}

	public IGeneticAnalyzer createAnalyzer(Window window, int xPos, int yPos, boolean rightBoarder, IGeneticAnalyzerProvider provider) {
		return new GeneticAnalyzer(window, xPos, yPos, rightBoarder, provider);
	}

	public static ContainerElement horizontal(int height, int spacing, Insets margin) {
		return (ContainerElement) new ContainerElement()
				.setLayout(FlexLayout.horizontal(spacing, margin))
				.setSize(GuiElement.UNKNOWN_WIDTH, height);
	}

	public static ContainerElement vertical(int width, int spacing, Insets margin) {
		return (ContainerElement) new ContainerElement()
				.setLayout(FlexLayout.vertical(spacing, margin))
				.setSize(width, GuiElement.UNKNOWN_HEIGHT);
	}

	public static ContainerElement horizontal(int height, int spacing) {
		return (ContainerElement) new ContainerElement()
				.setLayout(FlexLayout.horizontal(spacing))
				.setSize(GuiElement.UNKNOWN_WIDTH, height);
	}

	public static ContainerElement vertical(int width, int spacing) {
		return (ContainerElement) new ContainerElement()
				.setLayout(FlexLayout.vertical(spacing))
				.setSize(width, GuiElement.UNKNOWN_HEIGHT);
	}

	public static ContainerElement pane() {
		return new ContainerElement();
	}

	public static ContainerElement pane(int width, int height) {
		return (ContainerElement) new ContainerElement()
				.setSize(width, height);
	}

	public static ContainerElement pane(int xPos, int yPos, int width, int height) {
		return (ContainerElement) new ContainerElement()
				.setPreferredBounds(xPos, yPos, width, height);
	}

	public final int getColorCoding(boolean dominant) {
		if (dominant) {
			return ColourProperties.INSTANCE.get("gui.beealyzer.dominant");
		} else {
			return ColourProperties.INSTANCE.get("gui.beealyzer.recessive");
		}
	}

	public final Style getStateStyle(boolean dominant) {
		return dominant ? dominantStyle : recessiveStyle;
	}

	public final Style getGuiStyle() {
		return guiStyle;
	}

	public GuiElement createFertilityInfo(IAlleleValue<Integer> fertilityAllele, int texOffset) {
		String fertilityString = fertilityAllele.getValue() + " x";

		ContainerElement layout = horizontal(GuiElement.UNKNOWN_HEIGHT, 2);
		layout.label(fertilityString, getStateStyle(fertilityAllele.isDominant()));
		layout.drawable(0, -1, new Drawable(TEXTURE, 60, 240 + texOffset, 12, 8));
		return layout;
	}

	public GuiElement createToleranceInfo(IAlleleValue<EnumTolerance> toleranceAllele, IAlleleForestrySpecies species, String text) {
		ContainerElement layout = horizontal(GuiElement.UNKNOWN_HEIGHT, 0);
		layout.label(text, getStateStyle(species.isDominant()));
		layout.add(createToleranceInfo(toleranceAllele));
		return layout;
	}

	public GuiElement createToleranceInfo(IAlleleValue<EnumTolerance> toleranceAllele) {
		Style textStyle = getStateStyle(toleranceAllele.isDominant());
		EnumTolerance tolerance = toleranceAllele.getValue();
		ITextComponent component = null;

		ContainerElement layout = horizontal(GuiElement.UNKNOWN_HEIGHT, 2);
		switch (tolerance) {
			case BOTH_1:
			case BOTH_2:
			case BOTH_3:
			case BOTH_4:
			case BOTH_5:
				layout.add(createBothSymbol(0, -1));
				break;
			case DOWN_1:
			case DOWN_2:
			case DOWN_3:
			case DOWN_4:
			case DOWN_5:
				layout.add(createDownSymbol(0, -1));
				break;
			case UP_1:
			case UP_2:
			case UP_3:
			case UP_4:
			case UP_5:
				layout.add(createUpSymbol(0, -1));
				break;
			default:
				layout.add(createNoneSymbol(0, -1));
				component = new StringTextComponent("(0)");
				break;
		}
		if (component == null) {
			component = new StringTextComponent("(")
					.append(toleranceAllele.getDisplayName())
					.append(")");
		}
		layout.label(component).setStyle(textStyle);
		return layout;
	}

	public GuiElement createMutationResultant(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker) {
		if (breedingTracker.isDiscovered(mutation)) {
			ContainerElement element = new ContainerElement();
			element.setPreferredBounds(x, y, width, height);
			IAlyzerPlugin plugin = ((IForestrySpeciesRoot) mutation.getRoot()).getAlyzerPlugin();
			Map<ResourceLocation, ItemStack> iconStacks = plugin.getIconStacks();

			ItemStack firstPartner = iconStacks.get(mutation.getFirstParent().getRegistryName());
			ItemStack secondPartner = iconStacks.get(mutation.getSecondParent().getRegistryName());
			element.add(new ItemElement(0, 0, firstPartner), createProbabilityAdd(mutation, 21, 4), new ItemElement(33, 0, secondPartner));
			return element;
		}
		// Do not display secret undiscovered mutations.
		if (mutation.isSecret()) {
			return null;
		}

		return createUnknownMutationGroup(x, y, width, height, mutation);
	}

	@Nullable
	public ContainerElement createMutation(int x, int y, int width, int height, IMutation mutation, IAllele species, IBreedingTracker breedingTracker) {
		if (breedingTracker.isDiscovered(mutation)) {
			ContainerElement element = pane(x, y, width, height);
			IForestrySpeciesRoot speciesRoot = (IForestrySpeciesRoot) mutation.getRoot();
			int speciesIndex = speciesRoot.getKaryotype().getSpeciesType().ordinal();
			IDatabasePlugin plugin = speciesRoot.getSpeciesPlugin();
			Map<String, ItemStack> iconStacks = plugin.getIndividualStacks();

			ItemStack partner = iconStacks.get(mutation.getPartner(species).getRegistryName().toString());
			IAllele resultAllele = mutation.getTemplate()[speciesIndex];
			ItemStack result = iconStacks.get(resultAllele.getRegistryName().toString());
			element.add(new ItemElement(0, 0, partner), new ItemElement(33, 0, result));
			createProbabilityArrow(element, mutation, 18, 4, breedingTracker);
			return element;
		}
		// Do not display secret undiscovered mutations.
		if (mutation.isSecret()) {
			return null;
		}

		return createUnknownMutationGroup(x, y, width, height, mutation, breedingTracker);
	}

	private static ContainerElement createUnknownMutationGroup(int x, int y, int width, int height, IMutation mutation) {
		ContainerElement element = pane(x, y, width, height);

		element.add(createQuestionMark(0, 0), createProbabilityAdd(mutation, 21, 4), createQuestionMark(32, 0));
		return element;
	}

	private static ContainerElement createUnknownMutationGroup(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker) {
		ContainerElement element = pane(x, y, width, height);

		element.add(createQuestionMark(0, 0), createQuestionMark(32, 0));
		createProbabilityArrow(element, mutation, 18, 4, breedingTracker);
		return element;
	}

	private static void createProbabilityArrow(ContainerElement element, IMutation combination, int x, int y, IBreedingTracker breedingTracker) {
		float chance = combination.getBaseChance();
		int line = 247;
		int column = 100;
		switch (EnumMutateChance.rateChance(chance)) {
			case HIGHEST:
				column = 100;
				break;
			case HIGHER:
				column = 100 + 15;
				break;
			case HIGH:
				column = 100 + 15 * 2;
				break;
			case NORMAL:
				column = 100 + 15 * 3;
				break;
			case LOW:
				column = 100 + 15 * 4;
				break;
			case LOWEST:
				column = 100 + 15 * 5;
				break;
			default:
				break;
		}

		// Probability arrow
		element.drawable(x, y, new Drawable(TEXTURE, column, line, 15, 9));

		boolean researched = breedingTracker.isResearched(combination);
		if (researched) {
			element.label("+")
					.setStyle(GuiConstants.DEFAULT_STYLE)
					.setAlign(Alignment.TOP_LEFT)
				.setSize(10, 10)
				.setLocation(x + 9, y + 1);
		}
	}

	private static DrawableElement createProbabilityAdd(IMutation mutation, int x, int y) {
		float chance = mutation.getBaseChance();
		int line = 247;
		int column = 190;
		switch (EnumMutateChance.rateChance(chance)) {
			case HIGHEST:
				column = 190;
				break;
			case HIGHER:
				column = 190 + 9;
				break;
			case HIGH:
				column = 190 + 9 * 2;
				break;
			case NORMAL:
				column = 190 + 9 * 3;
				break;
			case LOW:
				column = 190 + 9 * 4;
				break;
			case LOWEST:
				column = 190 + 9 * 5;
				break;
			default:
				break;
		}

		// Probability add
		return new DrawableElement(x, y, new Drawable(TEXTURE, column, line, 9, 9));
	}

	private static DrawableElement createQuestionMark(int x, int y) {
		return new DrawableElement(x, y, QUESTION_MARK);
	}

	private static DrawableElement createDownSymbol(int x, int y) {
		return new DrawableElement(x, y, DOWN_SYMBOL);
	}

	private static DrawableElement createUpSymbol(int x, int y) {
		return new DrawableElement(x, y, UP_SYMBOL);
	}

	private static DrawableElement createBothSymbol(int x, int y) {
		return new DrawableElement(x, y, BOOTH_SYMBOL);
	}

	private static DrawableElement createNoneSymbol(int x, int y) {
		return new DrawableElement(x, y, NONE_SYMBOL);
	}
}
