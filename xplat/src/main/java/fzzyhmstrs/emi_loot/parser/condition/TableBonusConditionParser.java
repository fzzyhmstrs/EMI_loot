package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public class TableBonusConditionParser implements ConditionParser {

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative) {
        Enchantment enchant = ((TableBonusLootCondition)condition).enchantment();
        Text name = LText.enchant(enchant);

        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.table_bonus", name)));
    }
}