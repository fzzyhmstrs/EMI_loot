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
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static fzzyhmstrs.emi_loot.util.FloatTrimmer.trimFloatString;

public class MobLootRecipe implements EmiRecipe {

    private final MinecraftClient client = MinecraftClient.getInstance();
    
    public MobLootRecipe(ClientMobLootTable loot){
        this.loot = loot;
        allStacksGuaranteed = true;
        loot.build(MinecraftClient.getInstance().world);
        Identifier mobId = loot.mobId;
        System.out.println(mobId);
        EntityType<?> type = Registry.ENTITY_TYPE.get(mobId);
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
                ((SheepEntity)entity).setColor(DyeColor.byName(loot.color,DyeColor.WHITE));
            }
            double scale = 1.05 / len * 8.0;
            name = entity.getName();
            inputStack = EntityEmiStack.ofScaled(entity,scale);
        } else{
            inputStack = EmiStack.EMPTY;
            name = Text.translatable("emi_loot.missing_entity");
        }
        List<EmiStack> list = new LinkedList<>();
        loot.builtItems.forEach((textList, builtPool)->
            builtPool.map().forEach((poolList, map)->
                map.forEach((stack, weight)->{
                    if (weight < 100f){
                        allStacksGuaranteed = false;
                    }
                    list.add(EmiStack.of(stack));
                })
            )
        );
        outputStacks = list;
    }

    private final ClientMobLootTable loot;
    private final EmiStack inputStack;
    private final List<EmiStack> outputStacks;
    private boolean allStacksGuaranteed;
    private final Text name;
    

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
        return (19 * 4) + (26 * 3) + 25;
    }

    @Override
    public int getDisplayHeight() {
        AtomicInteger height = new AtomicInteger();
        height.addAndGet(28);
        loot.builtItems.forEach((poolList,pool)-> {
            poolList.forEach((text)->{
                List<OrderedText> subList = client.textRenderer.wrapLines(text,166);
                subList.forEach(subText -> height.addAndGet(9));
            });
            pool.map().forEach((categoryList,itemMap)-> {
                categoryList.forEach((text) -> {
                    List<OrderedText> subList = client.textRenderer.wrapLines(text, 166);
                    subList.forEach(subText -> height.addAndGet(9));
                });
                height.addAndGet(23);
            });
        });
        return height.get();
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int poolOffset = getDisplayHeight() > widgets.getHeight() ? 21 : 23;
        widgets.addSlot(inputStack,0,0).output(true);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 30, 8);
        widgets.addText(name.asOrderedText(),30,0,0x404040,false);
        var yObj = new Object() {
            int y = getDisplayHeight() > widgets.getHeight() ? 27 : 28;
        };
        loot.builtItems.forEach((poolList,pool)-> {
            poolList.forEach((text)->{
                List<OrderedText> subList = client.textRenderer.wrapLines(text,widgets.getWidth());
                subList.forEach(subText -> {
                    widgets.addText(subText,0 ,yObj.y,0x404040,false);
                    yObj.y +=9;
                });
            });
            pool.map().forEach((categoryList,itemMap)->{
                categoryList.forEach((text)->{
                    List<OrderedText> subList = client.textRenderer.wrapLines(text,widgets.getWidth());
                    subList.forEach(subText -> {
                        widgets.addText(subText,0 ,yObj.y,0x404040,false);
                        yObj.y +=9;
                    });
                });
                var xObj = new Object() {
                    int x = 0;
                };
                AtomicInteger index = new AtomicInteger();
                AtomicBoolean addedItems = new AtomicBoolean(false);
                itemMap.forEach((stack,weight)->{
                    widgets.addSlot(EmiStack.of(stack), xObj.x, yObj.y);
                    addedItems.set(true);
                    xObj.x += 20;
                    if (weight != 100F){
                        String fTrim = trimFloatString(weight);
                        widgets.addText(Text.translatable("emi_loot.percentage", fTrim).asOrderedText(), xObj.x, yObj.y,0x404040,false);
                        xObj.x += 26;
                    }
                    if (index.getAndIncrement() == 3){
                        yObj.y += getDisplayHeight() > widgets.getHeight() ? 19 : 20;
                        xObj.x = 0;
                        index.set(0);
                    }
                });
                if (index.get() == 0 && addedItems.get()){
                    yObj.y -= getDisplayHeight() > widgets.getHeight() ? 19 : 20;
                }
                yObj.y += poolOffset;
            });
        });
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
