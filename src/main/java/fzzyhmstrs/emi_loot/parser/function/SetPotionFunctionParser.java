package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.util.TextKey;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;

import java.util.List;

public class SetPotionFunctionParser{
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, List<TextKey> conditionTexts){
        Potion potion = ((SetPotionLootFunctionAccessor)function).getPotion();
        PotionUtil.setPotion(stack, potion);
        Text potionName = LText.translatable(potion.finishTranslationKey(Items.POTION.getTranslationKey() + ".effect."));
        return new LootFunctionResult(TextKey.of("emi_loot.function.potion",potionName.getString()), ItemStack.EMPTY,conditionsTexts);
    }
}
