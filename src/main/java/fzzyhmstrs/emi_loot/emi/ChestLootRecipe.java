package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientChestLootTable;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    }

    private final ClientChestLootTable loot;
    private final Map<EmiStack, Float> lootStacks;
    private boolean isGuaranteedNonChance = false;

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
        return 100;
    }

    @Override
    public int getDisplayHeight() {
        int titleHeight = 11;

        return 0;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

    }

    @Override
    public boolean supportsRecipeTree() {
        return EmiRecipe.super.supportsRecipeTree();
    }

    @Override
    public boolean hideCraftable() {
        return EmiRecipe.super.hideCraftable();
    }
}
