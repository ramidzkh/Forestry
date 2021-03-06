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
package forestry.core.models;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.utils.ResourceUtil;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractBakedModel implements IBakedModel {
	@Nullable
	protected ItemOverrideList overrideList;

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
		return Collections.emptyList();
	}

	protected ItemOverrideList createOverrides() {
		return ItemOverrideList.EMPTY;
	}

	@Override
	public ItemOverrideList getOverrides() {
		if (overrideList == null) {
			overrideList = createOverrides();
		}
		return overrideList;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return ResourceUtil.getMissingTexture();
	}

	@Override
	public ItemCameraTransforms getTransforms() {
		return ItemCameraTransforms.NO_TRANSFORMS;
	}

}
