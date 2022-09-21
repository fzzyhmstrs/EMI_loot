package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.util.Identifier;

public class EmiClientPlugin implements EmiPlugin {
    private static final Identifier LOOT_ID = new Identifier(EMILoot.MOD_ID,"chest_loot");
    public static final EmiRecipeCategory LOOT_CATEGORY = new EmiRecipeCategory(LOOT_ID, new LootSimplifiedRenderer(0,0));

    @Override
    public void register(EmiRegistry registry) {

        registry.addCategory(LOOT_CATEGORY);


    }
}
