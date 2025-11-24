package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.ApplyBonusLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.text.Text;

import java.util.List;

public class ApplyBonusFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        Enchantment enchant = ((ApplyBonusLootFunctionAccessor)function).getEnchantment();
        Text name = LText.enchant(enchant);
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.bonus", name), ItemStack.EMPTY, conditionTexts);
    }
}