package fzzyhmstrs.emi_loot.forge;

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
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class BlockRendererForge {

	/**
	 B2T: block to transformation
	 */
	public static final Function<BlockState, Transformation> B2T;
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
		// cheating a little...
		B2T = (state) -> {
			Block block = state.getBlock();
			return block instanceof FlowerBlock || block instanceof SaplingBlock || block instanceof CobwebBlock || block instanceof FernBlock || block instanceof SeagrassBlock || block instanceof TallPlantBlock ? T_CROSS : null;
		};
	}

	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			TICKED_BE.clear();
		}
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

		Transformation t = B2T.apply(blockState);
		Objects.requireNonNullElse(t, T_CUBE).apply(false, matrices);
		BlockRenderManager brm = MinecraftClient.getInstance().getBlockRenderManager();
		//DiffuseLighting.disableGuiDepthLighting();
		VertexConsumerProvider vcp = draw.getVertexConsumers();
		VertexConsumer consumer = vcp.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			Random random = Random.create();
			random.setSeed(42);
			consumer.light(LightmapTextureManager.MAX_LIGHT_COORDINATE);
			brm.renderBlock(blockState, blockPos, world, matrices, consumer, false, random);
		}
		FluidState fluidState = blockState.getFluidState();
		if (!fluidState.isEmpty()) {
			// TODO: render the fluid!
		}
		matrices.pop();
		draw.draw();
	}
}