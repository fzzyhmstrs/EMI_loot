package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import fzzyhmstrs.emi_loot.client.ClientMobLootTable;
import fzzyhmstrs.emi_loot.client.ClientResourceData;
import fzzyhmstrs.emi_loot.util.EntityEmiStack;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.WidgetRowBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MobLootRecipe implements EmiRecipe {

    //private final static Map<EntityType<?>,Integer> needsElevating;
    private static final Identifier ARROW_ID = new Identifier(EMILoot.MOD_ID,"textures/gui/downturn_arrow.png");

    public MobLootRecipe(ClientMobLootTable loot){
        this.loot = loot;
        loot.build(MinecraftClient.getInstance().world, Blocks.AIR);
        Identifier mobId = loot.mobId;
        EntityType<?> type = Registries.ENTITY_TYPE.get(mobId);
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
                ((SlimeEntity)entity).setSize(5,false);
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
            if (ClientResourceData.MOB_SCALES.containsKey(type)){
                scale *= ClientResourceData.MOB_SCALES.getOrDefault(type,1.0f);
            }
            inputStack = EntityEmiStack.ofScaled(entity,scale);
        } else{
            inputStack = EmiStack.EMPTY;
            name = LText.translatable("emi_loot.missing_entity");
        }
        List<EmiStack> list = new LinkedList<>();
        //System.out.println(getId());
        loot.builtItems.forEach((builtPool)-> {
                builtPool.stackMap().forEach((weight, stacks) -> {
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
    private final Text name;
    private final EntityType<?> type;
    private final List<WidgetRowBuilder> rowBuilderList = new LinkedList<>();

    private void addWidgetBuilders(ClientBuiltPool newPool, boolean recursive){
        if (recursive || rowBuilderList.isEmpty()){
            rowBuilderList.add(new WidgetRowBuilder(154));
        }
        boolean added = false;
        for (WidgetRowBuilder builder : rowBuilderList){
            if (builder.canAddPool(newPool)){
                builder.addAndTrim(newPool);
                added = true;
                break;
            }
        }
        if (!added){
            Optional<ClientBuiltPool> opt = rowBuilderList.get(rowBuilderList.size() - 1).addAndTrim(newPool);
            opt.ifPresent(clientMobBuiltPool -> addWidgetBuilders(clientMobBuiltPool, true));
        }


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
        if (rowBuilderList.size() > 1 || rowBuilderList.get(0).getWidth() > 94) {
            return 28 + 23 + 29 * (rowBuilderList.size() - 1);
        } else {
            return 34;
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int rowOffset = 29;
        int x = 0;
        int y = 0;
        //draw the mob
        if (!ClientResourceData.MOB_OFFSETS.containsKey(type)) {
            widgets.addSlot(inputStack, x, y).large(true);
        } else {
            int offset = ClientResourceData.MOB_OFFSETS.getOrDefault(type,0);
            widgets.addTexture(EmiTexture.LARGE_SLOT,x,y);
            widgets.addDrawable(x,y,16,16,(matrices,mx,my,delta)->inputStack.render(matrices,5, 6 + offset,delta));
        }
        widgets.addText(name.asOrderedText(),30,0,0x404040,false);
        if (rowBuilderList.size() == 1 && rowBuilderList.get(0).getWidth() <= 94){
            widgets.addTexture(new EmiTexture(ARROW_ID, 0, 16, 39, 15, 39, 15, 64, 32), 30, 10);
            x = 60;
            y = 11;
            WidgetRowBuilder builder = rowBuilderList.get(0);
            for (ClientBuiltPool pool: builder.getPoolList()){
                IconGroupEmiWidget widget = new IconGroupEmiWidget(x,y,pool);
                widgets.add(widget);
                x += widget.getWidth() + 6;
            }
        } else {
            widgets.addTexture(new EmiTexture(ARROW_ID, 0, 0, 39, 15, 39, 15, 64, 32), 30, 10);
            y += 28;
            for (WidgetRowBuilder builder: rowBuilderList){
                for (ClientBuiltPool pool: builder.getPoolList()){
                    IconGroupEmiWidget widget = new IconGroupEmiWidget(x,y,pool);
                    widgets.add(widget);
                    x += widget.getWidth() + 6;
                }
                y += 29;
                x = 0;
            }
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
