package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetPotionLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;

import java.util.List;

public class SetPotionFunctionParser implements FunctionParser {
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function,ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        Potion potion = ((SetPotionLootFunctionAccessor)function).getPotion().value();
        PotionUtil.setPotion(stack, potion);
        Text potionName = LText.translatable(potion.finishTranslationKey(Items.POTION.getTranslationKey() + ".effect."));
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.potion",potionName.getString()), ItemStack.EMPTY,conditionTexts);
    }
}
