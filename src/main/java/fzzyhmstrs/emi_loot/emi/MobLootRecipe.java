package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientMobLootTable;
import fzzyhmstrs.emi_loot.util.EntityEmiStack;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.WidgetRowBuilder;
import fzzyhmstrs.emi_loot.util.SlimeEntitySizeSetter;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MobLootRecipe implements EmiRecipe {

    private final static Map<EntityType<?>,Integer> needsElevating;

    static{
        Object2IntMap<EntityType<?>> map = new Object2IntOpenHashMap<>();
        map.put(EntityType.COD,-8);
        map.put(EntityType.CHICKEN,0);
        map.put(EntityType.SQUID,-8);
        map.put(EntityType.GLOW_SQUID,-8);
        map.put(EntityType.CAVE_SPIDER,-3);
        map.put(EntityType.SALMON,-6);
        map.put(EntityType.CAT,-3);
        map.put(EntityType.RABBIT,-8);
        map.put(EntityType.TROPICAL_FISH,-9);
        map.put(EntityType.PUFFERFISH,-12);
        map.put(EntityType.ENDERMAN,7);
        map.put(EntityType.DOLPHIN,-3);
        map.put(EntityType.PHANTOM,-3);
        map.put(EntityType.TURTLE,0);
        needsElevating = map;
    }

    
    public MobLootRecipe(ClientMobLootTable loot){
        this.loot = loot;
        allStacksGuaranteed = true;
        loot.build(MinecraftClient.getInstance().world);
        Identifier mobId = loot.mobId;
        EntityType<?> type = Registry.ENTITY_TYPE.get(mobId);
        this.type = type;
        MinecraftClient client = MinecraftClient.getInstance();
        Entity entity = type.create(client.world);
        if (entity != null) {
            Box box = entity.getBoundingBox();
            double len = box.getAverageSideLength();
            if (len > 1.05){
                len = (len + Math.sqrt(len))/2.0;
            }
            if (entity instanceof SlimeEntity){
                ((SlimeEntitySizeSetter)entity).setSlimeSize(5,false);
            }
            if (entity instanceof SheepEntity && !Objects.equals(loot.color, "")){
                DyeColor color = DyeColor.byName(loot.color,DyeColor.WHITE);
                MutableText colorName = LText.translatable("color.minecraft." + color.getName());
                name = LText.translatable("emi_loot.color_name",colorName.getString(),entity.getName().getString());
                ((SheepEntity)entity).setColor(color);
            } else {
                name = entity.getName();
            }
            double scale = 1.05 / len * 8.0;
            inputStack = EntityEmiStack.ofScaled(entity,scale);
        } else{
            inputStack = EmiStack.EMPTY;
            name = LText.translatable("emi_loot.missing_entity");
        }
        List<EmiStack> list = new LinkedList<>();
        loot.builtItems.forEach((builtPool)-> {
                builtPool.stackMap().forEach((weight, stacks) -> {
                    if (weight < 100f) {
                        allStacksGuaranteed = false;
                    }
                    list.addAll(stacks.getEmiStacks());
                });
                addWidgetBuilders(builtPool,false);
            }
        );
        outputStacks = list;
    }

    private final ClientMobLootTable loot;
    private final EmiStack inputStack;
    private final List<EmiStack> outputStacks;
    private boolean allStacksGuaranteed;
    private final Text name;
    private final EntityType<?> type;
    private final List<WidgetRowBuilder> rowBuilderList = new LinkedList<>();

    private void addWidgetBuilders(ClientMobLootTable.ClientMobBuiltPool newPool, boolean recursive){
        WidgetRowBuilder builder;
        if (recursive || rowBuilderList.isEmpty()){
            builder = new WidgetRowBuilder(154);
        } else {
            builder = rowBuilderList.get(rowBuilderList.size() - 1);
        }
        Optional<ClientMobLootTable.ClientMobBuiltPool> opt = builder.addAndTrim(newPool);
        rowBuilderList.add(builder);
        opt.ifPresent(clientMobBuiltPool -> addWidgetBuilders(clientMobBuiltPool, true));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiClientPlugin.MOB_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return new Identifier("emi", EMILootClient.MOD_ID + "/" + getCategory().id.getPath() + "/" + loot.id.getNamespace() + "/" + loot.id.getPath());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return new LinkedList<>();
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
                    return EmiRecipe.super.getCatalysts();
                }

    @Override
    public List<EmiStack> getOutputs() {
        return outputStacks;
    }

    @Override
    public int getDisplayWidth() {
        return 154;
    }

    @Override
    public int getDisplayHeight() {
        return 28 + 23 + 29 * (rowBuilderList.size() - 1);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int rowOffset = 29;
        int x = 0;
        int y = 0;
        //draw the mob
        if (!MobLootRecipe.needsElevating.containsKey(type)) {
            widgets.addSlot(inputStack, x, y).output(true);
        } else {
            int offset = MobLootRecipe.needsElevating.getOrDefault(type,0);
            widgets.addTexture(EmiTexture.LARGE_SLOT,x,y);
            widgets.addDrawable(x,y,16,16,(matrices,mx,my,delta)->inputStack.render(matrices,5,+ offset,delta));
        }

        y += 28;
        for (WidgetRowBuilder builder: rowBuilderList){
            for (ClientMobLootTable.ClientMobBuiltPool pool: builder.getPoolList()){
                IconGroupEmiWidget widget = new IconGroupEmiWidget(x,y,pool);
                widgets.add(widget);
                x += widget.getWidth() + 6;
            }
            y += 29;
            x = 0;
        }
    }

    //may revisit later
    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public boolean hideCraftable() {
        return EmiRecipe.super.hideCraftable();
    }
}
