package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientChestLootTable;
import net.minecraft.util.Identifier;

public class EmiClientPlugin implements EmiPlugin {
    private static final Identifier LOOT_ID = new Identifier(EMILoot.MOD_ID,"chest_loot");
    public static final EmiRecipeCategory LOOT_CATEGORY = new EmiRecipeCategory(LOOT_ID, new LootSimplifiedRenderer(0,0));

    @Override
    public void register(EmiRegistry registry) {

        registry.addCategory(LOOT_CATEGORY);

        EMILootClient.tables.getLoots().forEach(lootReceiver -> {
            if (lootReceiver instanceof ClientChestLootTable){
                registry.addRecipe(new ChestLootRecipe((ClientChestLootTable) lootReceiver));
            }
        });

    }
}
