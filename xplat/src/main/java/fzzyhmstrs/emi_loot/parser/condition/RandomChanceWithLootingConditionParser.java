package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.EnchantmentLevelBasedValueParser;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceWithEnchantedBonusLootCondition;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomChanceWithLootingConditionParser implements ConditionParser {

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative) {
        float chance = ((RandomChanceWithEnchantedBonusLootCondition)condition).unenchantedChance();
        EnchantmentLevelBasedValue multiplier = ((RandomChanceWithEnchantedBonusLootCondition)condition).enchantedChance();
        RegistryEntry<Enchantment> enchant = ((RandomChanceWithEnchantedBonusLootCondition)condition).enchantment();
        List<String> args = new ArrayList<>(Arrays.stream(new String[]{Float.toString((chance)), EnchantmentLevelBasedValueParser.parseValue(multiplier, "%", 100f).getString(), LText.enchant(enchant).getString()}).toList());
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.chance_looting", args)));
    }
}