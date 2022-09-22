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
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChestLootRecipe implements EmiRecipe {

    public ChestLootRecipe(ClientChestLootTable loot){
        this.loot = loot;
        if (loot.items.size() == 1){
            if (loot.items.values().stream().toList().get(0)== 1f){
                isGuaranteedNonChance = true;

            }
        }
        Map<EmiStack,Float> map = new HashMap<>();
        loot.items.forEach((item,weight)-> map.put(EmiStack.of(item),weight));
        lootStacks = map;
        String key = "emi_loot.chest." + loot.id.toString();
        Text text = Text.translatable(key);
        if (Objects.equals(text.getString(), key)){
            Optional<ModContainer> modNameOpt = FabricLoader.getInstance().getModContainer(loot.id.getNamespace());
            if (modNameOpt.isPresent()){
                ModContainer modContainer = modNameOpt.get();
                String modName = modContainer.getMetadata().getName();
                title = Text.translatable("emi_loot.chest.unknown_chest",modName);
            } else {
                Text unknown = Text.translatable("emi_loot.chest.unknown");
                title = Text.translatable("emi_loot.chest.unknown_chest", unknown.getString());
            }
        } else {
            title = text;
        }
    }

    private final ClientChestLootTable loot;
    private final Map<EmiStack, Float> lootStacks;
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
        return lootStacks.keySet().stream().toList();
    }

    @Override
    public int getDisplayWidth() {
        return (19 * 3) + (20 * 3);
    }

    @Override
    public int getDisplayHeight() {
        int titleHeight = 11;
        int boxesHeight = (int) Math.ceil(loot.items.size()/3.0);
        return titleHeight + boxesHeight;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        //widgets.addText(title.asOrderedText(),1,0,0xFFFFFF,true);
        widgets.addText(title.asOrderedText(),1,0,0x404040,false);
        AtomicInteger index = new AtomicInteger(1);
        lootStacks.forEach((stack, itemWeight)->{
            int row = (int) Math.ceil(index.get() /3.0) - 1;
            int column = (index.get() - 1) % 3;
            index.getAndIncrement();
            widgets.addSlot(stack,column * 39,row * 19).recipeContext(this);
            String f = Float.toString(itemWeight);
            int fDot = f.indexOf(".");
            String fTrim;
            if (fDot > 0) {
                fTrim = f.substring(0, Math.min(f.length() - 1, fDot + 2));
            } else {
                fTrim = f + ".00";
            }
            //widgets.addText(Text.translatable("emi_loot.percentage",fTrim).asOrderedText(),column * 39 + 19,row * 19,0xFFFFFF,true);
            widgets.addText(Text.translatable("emi_loot.percentage",fTrim).asOrderedText(),column * 39 + 19,row * 19,0x404040,false);
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
