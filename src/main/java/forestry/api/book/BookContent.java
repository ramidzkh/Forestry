/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.book;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.layouts.ContainerElement;

/**
 * The deserialized content data of a book page. It creates a gui element at the moment the entry gets opened.
 */
@OnlyIn(Dist.CLIENT)
public abstract class BookContent<D> {
	public String type;

	/**
	 * A data object that was deserialized from the json object of this content.
	 * Can be everything from an image to a crafting recipe.
	 */
	@Nullable
	public D data = null;

	/**
	 * The class of the data field. Used for the deserialization. If the result of this method is null the data field
	 * will be null too.
	 */
	@Nullable
	public abstract Class<? extends D> getDataClass();

	/**
	 * Called after the deserialization.
	 */
	public void onDeserialization() {
	}

	/**
	 * Adds the content to the page by adding a {@link GuiElement} or at the content to the previous element.
	 *
	 * @param page            The gui element that represents a book page
	 * @param factory
	 * @param previous        The content of the previous element.
	 * @param previousElement The element that was previously added to the page.
	 * @param pageHeight      The max height of the current page.
	 * @return True if you added an element.
	 */
	public abstract boolean addElements(ContainerElement page, GuiElementFactory factory, @Nullable BookContent<?> previous, @Nullable GuiElement previousElement, int pageHeight);
}
