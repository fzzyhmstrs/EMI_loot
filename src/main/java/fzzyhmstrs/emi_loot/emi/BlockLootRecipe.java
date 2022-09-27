package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.client.ClientBlockLootTable;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockLootRecipe implements EmiRecipe {

    public BlockLootRecipe(ClientBlockLootTable loot){
        Identifier lootId = loot.id;
        Identifier blockId = fromLootId(lootId);
        Block block = Registry.BLOCK.get(blockId);
        inputStack = EmiStack.of(block);
    }

    private final EmiStack inputStack;

    private Identifier fromLootId(Identifier lootId){
        //TODO
        return new Identifier("empty");
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return null;
    }

    @Override
    public @Nullable Identifier getId() {
        return null;
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
