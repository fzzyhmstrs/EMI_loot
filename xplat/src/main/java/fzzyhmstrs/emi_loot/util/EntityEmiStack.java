package fzzyhmstrs.emi_loot.util;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootAgnos;
import fzzyhmstrs.emi_loot.client.ClientResourceData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class EntityEmiStack extends EmiStack {
    private final @Nullable Entity entity;
    private final EntityRenderContext ctx;

    protected EntityEmiStack(@Nullable Entity entity) {
        this(entity, 8.0);
    }

    protected EntityEmiStack(@Nullable Entity entity, double scale) {
        this.entity = entity;
        if (entity != null) {
            boolean hasTransform = ClientResourceData.MOB_ROTATIONS.containsKey(entity.getType());
            Vector3f transform = ClientResourceData.MOB_ROTATIONS.getOrDefault(entity.getType(), new Vector3f(0, 0, 0)).mul(0.017453292F);
            ctx = new EntityRenderContext(scale, hasTransform, transform);
        } else {
            ctx = new EntityRenderContext(scale, false, new Vector3f(0, 0, 0));
        }
    }

    public static EntityEmiStack of(@Nullable Entity entity) {
        return new EntityEmiStack(entity);
    }

    public static EntityEmiStack ofScaled(@Nullable Entity entity, double scale) {
        return new EntityEmiStack(entity, scale);
    }

    @Override
    public EmiStack copy() {
        EntityEmiStack stack = new EntityEmiStack(entity);
        stack.setRemainder(getRemainder().copy());
        stack.comparison = comparison;
        return stack;
    }

    @Override
    public boolean isEmpty() {
        return entity == null;
    }

    @Override
    public void render(DrawContext matrices, int x, int y, float delta, int flags) {
        try {
            if (entity != null) {
                if (entity instanceof LivingEntity living)
                    renderEntity(matrices.getMatrices(), x + 8, (int) (y + 8 + ctx.size), ctx, living);
                else
                    renderEntity(matrices.getMatrices(), (int) (x + (2 * ctx.size / 2)), (int) (y + (2 * ctx.size)), ctx, entity);
            }
        } catch (Throwable e) {
            // woopsie doopsies
            if (EMILoot.DEBUG && Util.getMeasuringTimeMs() % 1000 == 0) {
                EMILoot.LOGGER.error("Rendering error for entity stack" + this.toString());
            }
        }
    }

    @Override
    public NbtCompound getNbt() {
        throw new UnsupportedOperationException("EntityEmiStack is not intended for NBT handling");
    }

    @Override
    public Object getKey() {
        return entity;
    }

    @Override
    public Identifier getId() {
        if (entity == null) throw new RuntimeException("Entity is null");
        return Registries.ENTITY_TYPE.getId(entity.getType());
    }

    @Override
    public List<Text> getTooltipText() {
        return List.of(getName());
    }

    @Override
    public List<TooltipComponent> getTooltip() {
        List<TooltipComponent> list = new ArrayList<>();
        if (entity != null) {
            list.addAll(getTooltipText().stream().map(Text::asOrderedText).map(TooltipComponent::of).toList());
            EmiTooltipComponents.appendModName(list, Registries.ENTITY_TYPE.getId(entity.getType()).getNamespace());
            if (!getRemainder().isEmpty()) {
                list.add(EmiTooltipComponents.getRemainderTooltipComponent(this));
            }
        }
        return list;
    }

    @Override
    public Text getName() {
        return entity != null ? entity.getName() : LText.literal("yet another missingno");
    }

    static void renderEntity(MatrixStack matrices, int x, int y, EntityRenderContext ctx, LivingEntity entity) {
        MinecraftClient client = MinecraftClient.getInstance();

        double width = client.getWindow().getScaledWidth();
        double height = client.getWindow().getScaledHeight();
        float mouseX = (float)(client.mouse.getX() * width / (double)client.getWindow().getWidth());
        float mouseY = (float)(client.mouse.getY() * height / (double)client.getWindow().getHeight());
        double posX = mouseX - width/2 + 63;
        if (Double.isNaN(posX)) return;
        double posY = mouseY - height/2;
        if (Double.isNaN(posY)) return;
        float f = (float)Math.atan(-posX / 40.0F);
        float g = (float)Math.atan(-posY / 40.0F);

        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        matrixStack.translate(x, y, 1050.0);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale((float) ctx.size, (float) ctx.size, (float) ctx.size);
        Quaternionf quaternion = new Quaternionf().rotateZ(3.1415927F);
        Quaternionf quaternion2 = new Quaternionf().rotateX(g * 20.0F * 0.017453292F * MathHelper.cos(ctx.transform.z) - f * 20.0F * 0.017453292F * MathHelper.sin(ctx.transform.z));
        if (ctx.hasTransform) {
            Quaternionf quaternion3 = new Quaternionf().rotateXYZ(ctx.transform.x, ctx.transform.y, ctx.transform.z);
            quaternion.mul(quaternion3);
        }

        quaternion.mul(quaternion2);
        matrixStack2.multiply(quaternion);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0F + (f * 20.0F * MathHelper.cos(ctx.transform.z) + (g * 20.0F * MathHelper.sin(ctx.transform.z)));
        float yaw = 180.0F + (f * 40.0F * MathHelper.cos(ctx.transform.z) + (g * 40.0F * MathHelper.sin(ctx.transform.z)));
        entity.setYaw(yaw);
        float pitch = (-g * 20.0F * MathHelper.cos(ctx.transform.z)) + (- f * 20.0F * MathHelper.sin(ctx.transform.z));
        entity.setPitch(pitch);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        try {
            RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate, 15728880));
        } finally {
            immediate.draw();
        }
        entityRenderDispatcher.setRenderShadows(true);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    static void renderEntity(MatrixStack matrices, int x, int y, EntityRenderContext ctx, Entity entity) {
        MinecraftClient client = MinecraftClient.getInstance();
        Mouse mouse = client.mouse;
        float w = 1920;
        float h = 1080;
        Screen screen = client.currentScreen;
        if (screen != null) {
            w = screen.width;
            h = screen.height;
        }
        float mouseX = (float) ((w + 51) - mouse.getX());
        float mouseY = (float) ((h + 75 - 50) - mouse.getY());
        float f = (float)Math.atan(mouseX / 40.0F);
        float g = (float)Math.atan(mouseY / 40.0F);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        matrixStack.translate(x, y, 1050.0);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale((float) ctx.size, (float) ctx.size, (float) ctx.size);
        Quaternionf quaternion = new Quaternionf().rotateZ(3.1415927F);
        Quaternionf quaternion2 = new Quaternionf().rotateX(g * 20.0F * 0.017453292F * MathHelper.cos(ctx.transform.z) - f * 20.0F * 0.017453292F * MathHelper.sin(ctx.transform.z));
        if (ctx.hasTransform) {
            Quaternionf quaternion3 = new Quaternionf().rotateXYZ(ctx.transform.x, ctx.transform.y, ctx.transform.z);
            quaternion.mul(quaternion3);
        }

        quaternion.mul(quaternion2);
        matrixStack2.multiply(quaternion);
        float i = entity.getYaw();
        float j = entity.getPitch();
        entity.setYaw(180.0F + (f * 40.0F * MathHelper.cos(ctx.transform.z) + (g * 40.0F * MathHelper.sin(ctx.transform.z))));
        entity.setPitch((-g * 20.0F * MathHelper.cos(ctx.transform.z)) + (- f * 20.0F * MathHelper.sin(ctx.transform.z)) );
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        try {
            RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate, 15728880));
        } finally {
            immediate.draw();
        }
        entityRenderDispatcher.setRenderShadows(true);
        entity.setYaw(i);
        entity.setPitch(j);
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    private record EntityRenderContext(double size, boolean hasTransform, Vector3f transform) {
        static EntityRenderContext EMPTY = new EntityRenderContext(8.0, false, new Vector3f(0, 0, 0));
    }
}