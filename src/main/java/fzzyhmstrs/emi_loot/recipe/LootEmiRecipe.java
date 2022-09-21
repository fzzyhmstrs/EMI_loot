package fzzyhmstrs.emi_loot.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.emi.EmiClientPlugin;
import fzzyhmstrs.emi_loot.emi.LootSimplifiedRenderer;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LootEmiRecipe implements EmiRecipe {

    private final LootTable table;

    public LootEmiRecipe(LootTable table){
        this.table = table;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiClientPlugin.LOOT_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return new Identifier("emi", EMILoot.MOD_ID + table.toString());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return null;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return EmiRecipe.super.getCatalysts();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return null;
    }

    @Override
    public int getDisplayWidth() {
        return 0;
    }

    @Override
    public int getDisplayHeight() {
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
