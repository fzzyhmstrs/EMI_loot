package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetEnchantmentsLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetEnchantmentsFunctionParser implements FunctionParser {
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function,ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        Map<Enchantment, LootNumberProvider> enchantments = ((SetEnchantmentsLootFunctionAccessor)function).getEnchantments();
        boolean add = ((SetEnchantmentsLootFunctionAccessor)function).getAdd();
        if (stack.isOf(Items.BOOK)){
            stack = new ItemStack(Items.ENCHANTED_BOOK);
            ItemStack finalStack = stack;
            enchantments.forEach((enchantment, provider)->{
                float rollAvg = NumberProcessors.getRollAvg(provider);
                EnchantedBookItem.addEnchantment(finalStack, new EnchantmentLevelEntry(enchantment, (int)rollAvg));
            });
            return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_enchant_book"), finalStack,conditionTexts);
        } else {
            Map<Enchantment,Integer> finalStackMap = new HashMap<>();
            if (add){
                Map<Enchantment, Integer> stackMap = EnchantmentHelper.get(stack);
                enchantments.forEach((enchantment, provider)->{
                    float rollAvg = NumberProcessors.getRollAvg(provider);
                    finalStackMap.put(enchantment,Math.max(((int)rollAvg) + stackMap.getOrDefault(enchantment,0),0));
                });
            } else {
                enchantments.forEach((enchantment, provider)->{
                    float rollAvg = NumberProcessors.getRollAvg(provider);
                    finalStackMap.put(enchantment,Math.max(((int)rollAvg),1));
                });
            }
            EnchantmentHelper.set(finalStackMap,stack);
            return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_enchant_item"), stack, conditionTexts);
        }
    }
}
