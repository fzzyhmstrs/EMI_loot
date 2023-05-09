package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.function.LootFunction;

import java.util.List;

public class EnchantWithLevelsFunctionParser implements FunctionParser {
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function,ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        if (stack.isOf(Items.BOOK)){
            stack = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(stack,new EnchantmentLevelEntry(EMILoot.RANDOM,1));
            return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.randomly_enchanted_book"), stack,conditionTexts);
        } else {
            if (!stack.isEmpty())
                stack.addEnchantment(EMILoot.RANDOM,1);
            return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.randomly_enchanted_item"), ItemStack.EMPTY,conditionTexts);
        }
    }
}
