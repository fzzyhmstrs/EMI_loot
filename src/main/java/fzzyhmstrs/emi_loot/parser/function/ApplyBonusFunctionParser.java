package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.ApplyBonusLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;

import java.util.List;

public class ApplyBonusFunctionParser implements FunctionParser{
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function,ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        Enchantment enchant = ((ApplyBonusLootFunctionAccessor)function).getEnchantment().value();
        String name = enchant.getName(1).getString();
        String nTrim;
        if (enchant.getMaxLevel() != 1) {
            nTrim = name.substring(0, name.length() - 2);
        } else {
            nTrim = name;
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.bonus",nTrim), ItemStack.EMPTY, conditionTexts);
    }
}
