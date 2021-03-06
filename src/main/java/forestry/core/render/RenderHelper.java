package forestry.core.render;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

public class RenderHelper {
	public static final Vector3f ORIGIN = new Vector3f(0.0F, 0.0F, 0.0F);

	// The current partial ticks
	public float partialTicks;
	// The current transformation
	public MatrixStack transformation;
	public IRenderTypeBuffer buffer;
	public int combinedLight;
	public int packetLight;
	public float rColor = 1.0f;
	public float bColor = 1.0f;
	public float gColor = 1.0f;
	public float alpha = 1.0f;

	@Nullable
	private ItemEntity dummyEntityItem;
	private long lastTick;

	private Vector3f baseRotation = ORIGIN;

	public void update(float partialTicks, MatrixStack transformation, IRenderTypeBuffer buffer, int combinedLight, int packetLight) {
		this.packetLight = packetLight;
		this.combinedLight = combinedLight;
		this.partialTicks = partialTicks;
		this.transformation = transformation;
		this.buffer = buffer;
	}

	private ItemEntity dummyItem(World world) {
		if (dummyEntityItem == null) {
			dummyEntityItem = new ItemEntity(world, 0, 0, 0);
		} else {
			dummyEntityItem.level = world;
		}
		return dummyEntityItem;
	}

	public void renderItem(ItemStack stack, World world) {
		ItemEntity dummyItem = dummyItem(world);
		dummyItem.setItem(stack);

		if (world.getGameTime() != lastTick) {
			lastTick = world.getGameTime();
			dummyItem.tick();
		}

		EntityRendererManager renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
		renderManager.render(dummyItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, transformation, buffer, combinedLight);

		dummyItem.level = null;
	}

	public void setRotation(Vector3f baseRotation) {
		this.baseRotation = baseRotation;
	}

	public void rotate(Quaternion rotation) {
		transformation.mulPose(rotation);
	}

	public void translate(double x, double y, double z) {
		transformation.translate(x, y, z);
	}

	public void scale(float x, float y, float z) {
		transformation.scale(x, y, z);
	}

	public void color(float rColor, float gColor, float bColor) {
		color(rColor, gColor, bColor, 1.0f);
	}

	public void color(float rColor, float gColor, float bColor, float alpha) {
		this.rColor = rColor;
		this.gColor = gColor;
		this.bColor = bColor;
		this.alpha = alpha;
	}

	public void pop() {
		transformation.popPose();
	}

	public void push() {
		transformation.pushPose();
	}

	public void renderModel(IVertexBuilder builder, ModelRenderer... renderers) {
		renderModel(builder, ORIGIN, renderers);
	}

	public void renderModel(ResourceLocation location, ModelRenderer... renderers) {
		renderModel(location, ORIGIN, renderers);
	}

	public void renderModel(IVertexBuilder builder, Vector3f rotation, ModelRenderer... renderers) {
		for (ModelRenderer renderer : renderers) {
			renderer.xRot = baseRotation.x() + rotation.x();
			renderer.yRot = baseRotation.y() + rotation.y();
			renderer.zRot = baseRotation.z() + rotation.z();
			renderer.render(transformation, builder, combinedLight, packetLight, rColor, gColor, bColor, alpha);
		}
	}

	public void renderModel(ResourceLocation location, Vector3f rotation, ModelRenderer... renderers) {
		renderModel(buffer.getBuffer(RenderType.entityCutout(location)), rotation, renderers);
	}
}
