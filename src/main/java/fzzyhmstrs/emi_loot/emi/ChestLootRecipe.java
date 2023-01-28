package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientChestLootTable;
import fzzyhmstrs.emi_loot.util.LText;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static fzzyhmstrs.emi_loot.util.FloatTrimmer.trimFloatString;

public class ChestLootRecipe implements EmiRecipe {

    public ChestLootRecipe(ClientChestLootTable loot){
        this.loot = loot;
        if (loot.items.size() == 1){
            if (loot.items.values().toFloatArray()[0] == 1f){
                isGuaranteedNonChance = true;
            }
        }

        Map<Float, List<EmiStack>> map2 = new TreeMap<>();
        List<EmiStack> outputsList = new LinkedList<>();
        loot.items.forEach((item,weight)-> {
            List<EmiStack> list = map2.getOrDefault(weight,new LinkedList<>());
            EmiStack stack = EmiStack.of(item);
            list.add(stack);
            map2.put(weight,list);
            outputsList.add(stack);
        });
        lootStacksSorted = map2;

        if (loot.items.size() > (24 * (EMILoot.config.chestLootCompact ? 2 : 1))) {
            this.lootStacksSortedSize = lootStacksSorted.keySet().size();
        } else {
            this.lootStacksSortedSize = loot.items.size();
        }


        outputs = outputsList;
        String key = "emi_loot.chest." + loot.id.toString();
        Text text = LText.translatable(key);
        if (Objects.equals(text.getString(), key)){
            Optional<ModContainer> modNameOpt = FabricLoader.getInstance().getModContainer(loot.id.getNamespace());
            if (modNameOpt.isPresent()){
                ModContainer modContainer = modNameOpt.get();
                String modName = modContainer.getMetadata().getName();
                title = LText.translatable("emi_loot.chest.unknown_chest",modName);
            } else {
                Text unknown = LText.translatable("emi_loot.chest.unknown");
                title = LText.translatable("emi_loot.chest.unknown_chest", unknown.getString());
            }
        } else {
            title = text;
        }
    }

    private final ClientChestLootTable loot;
    //private final Map<EmiStack, Float> lootStacks;
    private final Map<Float,List<EmiStack>> lootStacksSorted;
    private final int lootStacksSortedSize;
    private final List<EmiStack> outputs;
    private boolean isGuaranteedNonChance = false;
    private final Text title;
    private final double columns = (EMILoot.config.chestLootCompact ? 8.0 : 4.0);

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiClientPlugin.LOOT_CATEGORY;
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
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        if (EMILoot.config.chestLootCompact) return 144;
        return (19 * 4) + (26 * 3) + 25;
    }

    @Override
    public int getDisplayHeight() {
        int titleHeight = 11;
        int boxesHeight = ((int) Math.ceil(lootStacksSortedSize/ columns) * (EMILoot.config.chestLootCompact ? 18 : 19)) - 1;
        return titleHeight + boxesHeight;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int titleSpace;
        final int finalRowHeight;
        if (widgets.getHeight() < getDisplayHeight()){
            titleSpace = 9;
            finalRowHeight = (widgets.getHeight() - titleSpace) / ((int) Math.ceil(lootStacksSortedSize / (EMILoot.config.chestLootCompact ? 8.0 : 4.0)));
        } else {
            titleSpace = 11;
            finalRowHeight = (EMILoot.config.chestLootCompact ? 18 : 19);
        }
        widgets.addText(title.asOrderedText(),1,0,0x404040,false);
        AtomicInteger index = new AtomicInteger(lootStacksSortedSize);
        lootStacksSorted.forEach((weight, items)->{
            if ((loot.items.size() <= (EMILoot.config.chestLootCompact ? 48 : 24)) && !EMILoot.config.chestLootAlwaysStackSame) {
                items.forEach((stack) -> {
                    System.out.println(stack.getName());
                    int row = (int) Math.ceil(index.get() / columns) - 1;
                    int column = (index.get() - 1) % (int)columns;
                    System.out.println(index.get());
                    System.out.println(row);
                    System.out.println(column);
                    index.getAndDecrement();
                    String fTrim = trimFloatString(weight);
                    SlotWidget slotWidget = new SlotWidget(stack, column * (EMILoot.config.chestLootCompact ? 18 : 45), titleSpace + row * finalRowHeight).recipeContext(this);
                    if (EMILoot.config.chestLootCompact) {
                        widgets.add(slotWidget.appendTooltip(LText.translatable("emi_loot.percentage", fTrim)));
                    } else {
                        widgets.add(slotWidget);
                        widgets.addText(LText.translatable("emi_loot.percentage", fTrim).asOrderedText(), column * 45 + 19, titleSpace + row * finalRowHeight, 0x404040, false);
                    }
                });
            } else {
                int row = (int) Math.ceil(index.get() / columns) - 1;
                int column = (index.get() - 1) % 4;
                index.getAndDecrement();
                EmiIngredient ingredient = EmiIngredient.of(items);
                String fTrim = trimFloatString(Math.min(weight/100f,0.01f));
                SlotWidget slotWidget = new SlotWidget(ingredient, column * (EMILoot.config.chestLootCompact ? 18 : 45), titleSpace + row * finalRowHeight).recipeContext(this);
                if (EMILoot.config.chestLootCompact) {
                    widgets.add(slotWidget.appendTooltip(LText.translatable("emi_loot.rolls", fTrim).formatted(Formatting.ITALIC,Formatting.GOLD)));
                } else {
                    widgets.add(slotWidget);
                    widgets.addText(LText.translatable("emi_loot.rolls_visible", fTrim).asOrderedText(), column * 45 + 19, titleSpace + row * finalRowHeight, 0x404040, false);
                }
            }
        });
    }

    @Override
    public boolean supportsRecipeTree() {
        return EmiRecipe.super.supportsRecipeTree() && isGuaranteedNonChance;
    }

    @Override
    public boolean hideCraftable() {
        return EmiRecipe.super.hideCraftable();
    }
}
