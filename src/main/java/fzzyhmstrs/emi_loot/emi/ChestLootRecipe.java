package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientChestLootTable;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static fzzyhmstrs.emi_loot.util.FloatTrimmer.trimFloatString;

public class ChestLootRecipe implements EmiRecipe {

    public ChestLootRecipe(ClientChestLootTable loot){
        this.loot = loot;
        if (loot.items.size() == 1){
            if (loot.items.values().stream().toList().get(0)== 1f){
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
        if (loot.items.size() > 24){
            this.lootStacksSortedSize = lootStacksSorted.keySet().size();
        } else{
            this.lootStacksSortedSize = loot.items.size();
        }

        outputs = outputsList;
        String key = "emi_loot.chest." + loot.id.toString();
        Text text = new TranslatableText(key);
        if (Objects.equals(text.getString(), key)){
            Optional<ModContainer> modNameOpt = FabricLoader.getInstance().getModContainer(loot.id.getNamespace());
            if (modNameOpt.isPresent()){
                ModContainer modContainer = modNameOpt.get();
                String modName = modContainer.getMetadata().getName();
                title = new TranslatableText("emi_loot.chest.unknown_chest",modName);
            } else {
                Text unknown = new TranslatableText("emi_loot.chest.unknown");
                title = new TranslatableText("emi_loot.chest.unknown_chest", unknown.getString());
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
        return (19 * 4) + (26 * 3) + 25;
    }

    @Override
    public int getDisplayHeight() {
        int titleHeight = 11;
        int boxesHeight = ((int) Math.ceil(lootStacksSortedSize/4.0) * 19) - 1;
        return titleHeight + boxesHeight;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int titleSpace;
        final int finalRowHeight;
        if (widgets.getHeight() < getDisplayHeight()){
            titleSpace = 9;
            finalRowHeight = (widgets.getHeight() - titleSpace)/((int) Math.ceil(loot.items.size()/4.0));
        } else {
            titleSpace = 11;
            finalRowHeight = 19;
        }
        widgets.addText(title.asOrderedText(),1,0,0x404040,false);
        AtomicInteger index = new AtomicInteger(lootStacksSortedSize);
        lootStacksSorted.forEach((weight, items)->{
            if (loot.items.size() <= 24) {
                items.forEach((stack) -> {
                    int row = (int) Math.ceil(index.get() / 4.0) - 1;
                    int column = (index.get() - 1) % 4;
                    index.getAndDecrement();
                    String fTrim = trimFloatString(weight);
                    widgets.addSlot(stack, column * 45, titleSpace + row * finalRowHeight).recipeContext(this);
                    widgets.addText(new TranslatableText("emi_loot.percentage", fTrim).asOrderedText(), column * 45 + 19, titleSpace + row * finalRowHeight, 0x404040, false);
                });
            } else {
                int row = (int) Math.ceil(index.get() / 4.0) - 1;
                int column = (index.get() - 1) % 4;
                index.getAndDecrement();
                EmiIngredient ingredient = EmiIngredient.of(items);
                String fTrim = trimFloatString(weight);
                widgets.addSlot(ingredient, column * 45, titleSpace + row * finalRowHeight).recipeContext(this);
                widgets.addText(new TranslatableText("emi_loot.percentage", fTrim).asOrderedText(), column * 45 + 19, titleSpace + row * finalRowHeight, 0x404040, false);
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
