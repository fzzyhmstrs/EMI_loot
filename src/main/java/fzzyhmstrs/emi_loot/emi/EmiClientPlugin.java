package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.block.Blocks;

public class EmiClientPlugin implements EmiPlugin {

    private final EmiStack LOOT_WORKSTATION = EmiStack.of(Blocks.CHEST.asItem());
    private final EmiRecipeCategory LOOT_CATEGORY = new EmiRecipeCategory(LOOT_WORKSTATION.getId(),LOOT_WORKSTATION, new LootSimplifiedRenderer(0,0));

    @Override
    public void register(EmiRegistry registry) {

    }
}
