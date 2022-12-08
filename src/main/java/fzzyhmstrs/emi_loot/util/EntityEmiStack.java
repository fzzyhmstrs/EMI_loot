package fzzyhmstrs.emi_loot.util;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.screen.tooltip.RemainderTooltipComponent;
import fzzyhmstrs.emi_loot.client.ClientResourceData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class EntityEmiStack extends EmiStack {
    private final @Nullable Entity entity;
    private final EntityEntry entry;
    private final EntityRenderContext ctx;

    protected EntityEmiStack(@Nullable Entity entity) {
        this(entity,8.0);
    }

    protected EntityEmiStack(@Nullable Entity entity, double scale) {
        this.entity = entity;
        this.entry = new EntityEntry(entity);
        if (entity != null) {
            boolean hasTransform = ClientResourceData.MOB_ROTATIONS.containsKey(entity.getType());
            Vec3f transform = ClientResourceData.MOB_ROTATIONS.getOrDefault(entity.getType(),Vec3f.ZERO);
            ctx = new EntityRenderContext(scale,hasTransform,transform);
        } else {
            ctx = new EntityRenderContext(scale,false,Vec3f.ZERO);
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
    public void render(MatrixStack matrices, int x, int y, float delta, int flags) {
        if (entity != null) {
            if (entity instanceof LivingEntity living)
                renderEntity(x + 8, (int) (y + 8 + ctx.size), ctx, living);
            else
                renderEntity((int) (x + (2 * ctx.size / 2)), (int) (y + (2 * ctx.size)), ctx, entity);
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
    public Entry<?> getEntry() {
        return entry;
    }

    @Override
    public Identifier getId() {
        if (entity == null) throw new RuntimeException("Entity is null");
        return Registry.ENTITY_TYPE.getId(entity.getType());
    }

    @Override
    public List<Text> getTooltipText() {
        return List.of(getName());
    }

    @Override
    public List<TooltipComponent> getTooltip() {
        List<TooltipComponent> list = new ArrayList<>();
        if (entity != null) {
            list.addAll(getTooltipText().stream().map(EmiPort::ordered).map(TooltipComponent::of).toList());
            String mod = EmiUtil.getModName(Registry.ENTITY_TYPE.getId(entity.getType()).getNamespace());
            list.add(TooltipComponent.of(EmiPort.ordered(EmiPort.literal(mod, Formatting.BLUE, Formatting.ITALIC))));
            if (!getRemainder().isEmpty()) {
                list.add(new RemainderTooltipComponent(this));
            }
        }
        return list;
    }

    @Override
    public Text getName() {
        return entity != null ? entity.getName() : EmiPort.literal("yet another missingno");
    }

    public static void renderEntity(int x, int y, EntityRenderContext ctx, LivingEntity entity) {
        MinecraftClient client = MinecraftClient.getInstance();

        double width = client.getWindow().getScaledWidth();
        double height = client.getWindow().getScaledHeight();
        float mouseX = (float)(client.mouse.getX() * width / (double)client.getWindow().getWidth());
        float mouseY = (float)(client.mouse.getY() * height / (double)client.getWindow().getHeight());
        double posX = mouseX - width/2 + 63;
        double posY = mouseY - height/2;
        float f = (float)Math.atan(-posX / 40.0F);
        float g = (float)Math.atan(-posY / 40.0F);

        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 1050.0);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale((float) ctx.size, (float) ctx.size, (float) ctx.size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F * MathHelper.cos(ctx.transform.getZ() * MathHelper.PI / 180) - f * 20.0F * MathHelper.sin(ctx.transform.getZ() * MathHelper.PI / 180));
        if (ctx.hasTransform){
            Quaternion quaternion3 = new Quaternion(ctx.transform.getX(),ctx.transform.getY(),ctx.transform.getZ(),true);
            quaternion.hamiltonProduct(quaternion3);
        }

        quaternion.hamiltonProduct(quaternion2);
        matrixStack2.multiply(quaternion);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0F + (f * 20.0F * MathHelper.cos(ctx.transform.getZ() * MathHelper.PI / 180) + (g * 20.0F * MathHelper.sin(ctx.transform.getZ() * MathHelper.PI / 180)));
        entity.setYaw(180.0F + (f * 40.0F * MathHelper.cos(ctx.transform.getZ() * MathHelper.PI / 180) + (g * 40.0F * MathHelper.sin(ctx.transform.getZ() * MathHelper.PI / 180))));
        entity.setPitch((-g * 20.0F * MathHelper.cos(ctx.transform.getZ() * MathHelper.PI / 180)) + (- f * 20.0F * MathHelper.sin(ctx.transform.getZ() * MathHelper.PI / 180)) );
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate, 15728880));
        immediate.draw();
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

    public static void renderEntity(int x, int y, EntityRenderContext ctx, Entity entity) {
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
        matrixStack.translate(x, y, 1050.0);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale((float) ctx.size, (float) ctx.size, (float) ctx.size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F * MathHelper.cos(ctx.transform.getZ() * MathHelper.PI / 180) - f * 20.0F * MathHelper.sin(ctx.transform.getZ() * MathHelper.PI / 180));
        if (ctx.hasTransform){
            Quaternion quaternion3 = new Quaternion(ctx.transform.getX(),ctx.transform.getY(),ctx.transform.getZ(),true);
            quaternion.hamiltonProduct(quaternion3);
        }

        quaternion.hamiltonProduct(quaternion2);
        matrixStack2.multiply(quaternion);
        float i = entity.getYaw();
        float j = entity.getPitch();
        entity.setYaw(180.0F + (f * 40.0F * MathHelper.cos(ctx.transform.getZ() * MathHelper.PI / 180) + (g * 40.0F * MathHelper.sin(ctx.transform.getZ() * MathHelper.PI / 180))));
        entity.setPitch((-g * 20.0F * MathHelper.cos(ctx.transform.getZ() * MathHelper.PI / 180)) + (- f * 20.0F * MathHelper.sin(ctx.transform.getZ() * MathHelper.PI / 180)) );
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate, 15728880));
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        entity.setYaw(i);
        entity.setPitch(j);
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    private record EntityRenderContext(double size, boolean hasTransform, Vec3f transform){
        static EntityRenderContext EMPTY = new EntityRenderContext(8.0,false,Vec3f.ZERO);
    }

    public static class EntityEntry extends Entry<Entity> {
        public EntityEntry(Entity value) {
            super(value);
        }

        @Override
        public Class<? extends Entity> getType() {
            return getValue().getType().getBaseClass();
        }
    }
}
