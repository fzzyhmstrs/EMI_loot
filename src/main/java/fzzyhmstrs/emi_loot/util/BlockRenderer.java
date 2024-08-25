package fzzyhmstrs.emi_loot.util;

import fzzyhmstrs.emi_loot.EMILoot;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.block.FernBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SeagrassBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BlockRenderer {

	/**
	 B2T: block to transformation
	 */
	public static final BlockApiLookup<Transformation, @Nullable Void> B2T = BlockApiLookup.get(EMILoot.identity("transformation"), Transformation.class, Void.class);
	/**
	 T_CUBE: Transformation cube
	 */
	public static final Transformation T_CUBE = new Transformation(new Vector3f(30, 210, 0), new Vector3f(0.925f, -0.8125f, 0), new Vector3f(0.625f));
	/**
	 T_CUBE: Transformation cross
	 */
	public static final Transformation T_CROSS = new Transformation(new Vector3f(15, 195, 0), new Vector3f(1, -0.95f, 0), new Vector3f(0.8f));
	/**
	 T_CUBE: ticked block entity
	 */
	public static final Set<BlockEntity> TICKED_BE = new HashSet<>();
	static {
		B2T.registerFallback((world, pos, state, blockEntity, context) -> {
			Block block = state.getBlock();
			return block instanceof FlowerBlock || block instanceof SaplingBlock || block instanceof CobwebBlock || block instanceof FernBlock || block instanceof SeagrassBlock || block instanceof TallPlantBlock ? T_CROSS : null;
		});
		ClientTickEvents.START_CLIENT_TICK.register(client -> TICKED_BE.clear());
	}

	public static void render(BlockState blockState, DrawContext draw, int x, int y, float delta) {
		MatrixStack matrices = draw.getMatrices();
		matrices.push();
		matrices.translate(x, y, 150);
		matrices.multiplyPositionMatrix(new Matrix4f().scaling(1, -1, 1));
		matrices.scale(16, 16, 16);
		World world = MinecraftClient.getInstance().world;
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		BlockPos blockPos = player != null ? player.getBlockPos() : BlockPos.ORIGIN;

		Transformation t = B2T.find(world, blockPos, blockState, null, null);
		Objects.requireNonNullElse(t, T_CUBE).apply(false, matrices);
		BlockRenderManager brm = MinecraftClient.getInstance().getBlockRenderManager();
		//DiffuseLighting.disableGuiDepthLighting();
		VertexConsumerProvider vcp = draw.getVertexConsumers();
		VertexConsumer consumer = vcp.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			Random random = Random.create();
			random.setSeed(42);
			brm.renderBlock(blockState, blockPos, world, matrices, consumer, false, random);
		}
		FluidState fluidState = blockState.getFluidState();
		if (!fluidState.isEmpty()) {
			render(fluidState, world, blockPos, matrices, consumer);
		}
		matrices.pop();
		draw.draw();
	}
	private static void render(FluidState fluidState, @Nullable World world, @Nullable BlockPos pos, MatrixStack matrices, VertexConsumer consumer) {
		FluidRenderHandler renderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluidState.getFluid());
		int color = renderHandler.getFluidColor(world, pos, fluidState);
		Sprite[] sprites = renderHandler.getFluidSprites(world, pos, fluidState);
		Sprite still = sprites[0];
		Sprite flowing = sprites[1];
		float red = (color >> 16 & 0xFF) / 255f;
		float green = (color >> 8 & 0xFF) / 255f;
		float blue = (color & 0xFF) / 255f;
		@SuppressWarnings("DataFlowIssue")
		var meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
		BakedQuad quad;
		QuadEmitter emitter = meshBuilder.getEmitter();
		quad = emitter.square(Direction.UP, 0, 0, 1, 1, 2 / 16f)
				.spriteBake(still, MutableQuadView.BAKE_LOCK_UV)
				.color(color, color, color, color)
				.toBakedQuad(still);
		MatrixStack.Entry peek = matrices.peek();
		consumer.quad(peek, quad, red, green, blue, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
		quad = emitter.square(Direction.DOWN, 0, 0, 1, 1, 0)
				.spriteBake(still, MutableQuadView.BAKE_LOCK_UV)
				.color(color, color, color, color)
				.toBakedQuad(still);
		consumer.quad(peek, quad, red, green, blue, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
		for (Direction direction : Direction.Type.HORIZONTAL) {
			quad = emitter.square(direction, 0, 0, 1, 14 / 16f, 0)
					.spriteBake(flowing, MutableQuadView.BAKE_LOCK_UV)
					.color(color, color, color, color)
					.toBakedQuad(flowing);
			consumer.quad(peek, quad, red, green, blue, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
		}
	}

	private BlockRenderer() {}
}