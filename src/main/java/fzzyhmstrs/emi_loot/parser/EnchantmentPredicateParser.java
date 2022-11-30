package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.mixins.EnchantmentPredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public class EnchantmentPredicateParser{

    public static Text parseEnchantmentPredicates(List<EnchantmentPredicate> list){
        int size = list.size();
        if (size == 1){
            Enchantment enchant = ((EnchantmentPredicateAccessor)list.get(0)).getEnchantment();
            return LText.translatable("emi_loot.item_predicate.enchant",enchant.getName(1).getString());
        }
        if (size == 2){
            Enchantment enchant1 = ((EnchantmentPredicateAccessor)list.get(0)).getEnchantment();
            Enchantment enchant2 = ((EnchantmentPredicateAccessor)list.get(1)).getEnchantment();
            return LText.translatable("emi_loot.item_predicate.enchant_2",enchant1.getName(1).getString(),enchant2.getName(1).getString());
        }
        if (size > 2){
            MutableText finalText = LText.empty();
            for (int i = 0; i < size; i++){
                Enchantment enchant = ((EnchantmentPredicateAccessor)list.get(i)).getEnchantment();
                if (i == 0){
                    finalText.append(LText.translatable("emi_loot.item_predicate.enchant_3a", enchant.getName(1).getString()));
                } else if (i == size - 2){
                    finalText.append(LText.translatable("emi_loot.item_predicate.enchant_3c", enchant.getName(1).getString()));
                } else if (i == size - 1){
                    finalText.append(LText.translatable("emi_loot.item_predicate.enchant_3d", enchant.getName(1).getString()));
                } else {
                    finalText.append(LText.translatable("emi_loot.item_predicate.enchant_3b",enchant.getName(1).getString()));
                }
            }
            return finalText;
        }    
    }
}
