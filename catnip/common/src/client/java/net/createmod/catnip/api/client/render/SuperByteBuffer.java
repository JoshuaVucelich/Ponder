package net.createmod.catnip.api.client.render;

import org.joml.Matrix3fc;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.api.theme.Color;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.util.LightCoordsUtil;
import org.joml.Matrix4f;

/**
 * Byte buffer helper for replaying baked models with local transforms.
 * Extends Flywheel {@link TransformStack} so Create can chain translate/rotate helpers on buffers.
 */
public interface SuperByteBuffer extends TransformStack<SuperByteBuffer> {
	static int maxLight(int packedLight1, int packedLight2) {
		int blockLight1 = LightCoordsUtil.block(packedLight1);
		int skyLight1 = LightCoordsUtil.sky(packedLight1);
		int blockLight2 = LightCoordsUtil.block(packedLight2);
		int skyLight2 = LightCoordsUtil.sky(packedLight2);
		return LightCoordsUtil.pack(Math.max(blockLight1, blockLight2), Math.max(skyLight1, skyLight2));
	}

	void renderInto(PoseStack ms, VertexConsumer consumer);

	boolean isEmpty();

	PoseStack getTransforms();

	<Self extends SuperByteBuffer> Self reset();

	<Self extends SuperByteBuffer> Self color(int color);

	<Self extends SuperByteBuffer> Self color(int r, int g, int b, int a);

	<Self extends SuperByteBuffer> Self disableDiffuse();

	<Self extends SuperByteBuffer> Self shiftUV(SpriteShiftEntry entry);

	<Self extends SuperByteBuffer> Self shiftUVScrolling(SpriteShiftEntry entry, float scrollU, float scrollV);

	<Self extends SuperByteBuffer> Self shiftUVtoSheet(SpriteShiftEntry entry, float uTarget, float vTarget, int sheetSize);

	<Self extends SuperByteBuffer> Self overlay(int overlay);

	<Self extends SuperByteBuffer> Self light(int packedLight);

	/**
	 * Indicate that this buffer should look up the light coordinates in the level.
	 */
	<Self extends SuperByteBuffer> Self useLevelLight(BlockAndTintGetter level);

	/**
	 * Indicate that this buffer should look up the light coordinates in the level.
	 * Light Positions will be transformed by the passed Matrix before the lookup.
	 */
	<Self extends SuperByteBuffer> Self useLevelLight(BlockAndTintGetter level, Matrix4f lightTransform);

	// --- TransformStack / Affine (delegate to local PoseStack) ---

	@Override
	default SuperByteBuffer pushPose() {
		getTransforms().pushPose();
		return this;
	}

	@Override
	default SuperByteBuffer popPose() {
		getTransforms().popPose();
		return this;
	}

	@Override
	default SuperByteBuffer mulPose(Matrix4fc pose) {
		getTransforms().last().pose().mul(pose);
		return this;
	}

	@Override
	default SuperByteBuffer mulNormal(Matrix3fc normal) {
		getTransforms().last().normal().mul(normal);
		return this;
	}

	@Override
	default SuperByteBuffer translate(float x, float y, float z) {
		getTransforms().translate(x, y, z);
		return this;
	}

	@Override
	default SuperByteBuffer rotate(Quaternionfc quaternion) {
		PoseStack.Pose pose = getTransforms().last();
		pose.pose().rotate(quaternion);
		pose.normal().rotate(quaternion);
		return this;
	}

	@Override
	default SuperByteBuffer rotateAround(Quaternionfc quaternion, float x, float y, float z) {
		PoseStack.Pose pose = getTransforms().last();
		pose.pose().rotateAround(quaternion, x, y, z);
		pose.normal().rotate(quaternion);
		return this;
	}

	@Override
	default SuperByteBuffer scale(float factorX, float factorY, float factorZ) {
		getTransforms().scale(factorX, factorY, factorZ);
		return this;
	}

	//

	default void delete() {
	}

	default <Self extends SuperByteBuffer> Self color(Color color) {
		return this.color(
			color.getRed(),
			color.getGreen(),
			color.getBlue(),
			color.getAlpha()
		);
	}

	default <Self extends SuperByteBuffer> Self shiftUVScrolling(SpriteShiftEntry entry, float scrollV) {
		return this.shiftUVScrolling(entry, 0, scrollV);
	}

	@FunctionalInterface
	interface SpriteShiftFunc {
		void shift(float u, float v, Output output);

		interface Output {
			void accept(float u, float v);
		}
	}

	class ShiftOutput implements SpriteShiftFunc.Output {
		public float u;
		public float v;

		@Override
		public void accept(float u, float v) {
			this.u = u;
			this.v = v;
		}
	}
}
