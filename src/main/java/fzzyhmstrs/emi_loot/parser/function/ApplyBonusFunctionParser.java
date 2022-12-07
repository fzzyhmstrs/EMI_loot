package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.util.TextKey;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.enchantment.Enchantment;

import java.util.List;

public class ApplyBonusFunctionParser{
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, List<TextKey> conditionTexts){
        Enchantment enchant = ((ApplyBonusLootFunctionAccessor)function).getEnchantment();
        String name = enchant.getName(1).getString();
        String nTrim;
        if (enchant.getMaxLevel() != 1) {
            nTrim = name.substring(0, name.length() - 2);
        } else {
            nTrim = name;
        }
        return new LootFunctionResult(TextKey.of("emi_loot.function.bonus",nTrim),ItemStack.EMPTY,conditionsTexts);
    }
}
