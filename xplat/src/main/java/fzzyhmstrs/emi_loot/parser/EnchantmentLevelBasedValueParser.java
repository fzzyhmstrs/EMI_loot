package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.text.Text;

public class EnchantmentLevelBasedValueParser {

	public static Text parseValue(EnchantmentLevelBasedValue value) {
		return parseValue(value, "", 1f);
	}

	public static Text parseValue(EnchantmentLevelBasedValue value, String percent, float multiplier) {
		if (value instanceof EnchantmentLevelBasedValue.Constant constant) {
			return LText.literal((constant.value() * multiplier) + percent);
		} else if (value instanceof EnchantmentLevelBasedValue.Lookup lookup) {
			return LText.translatable("emi_loot.enchantment_value.lookup", ListProcessors.buildOrList(lookup.values().stream().map(f -> LText.literal((f * multiplier) + percent)).toList()));
		} else if (value instanceof EnchantmentLevelBasedValue.Linear linear) {
			if (linear.base() == linear.perLevelAboveFirst()) {
				return LText.translatable("emi_loot.enchantment_value.linear.simple", (linear.base() * multiplier) + percent);
			}
			return LText.translatable("emi_loot.enchantment_value.linear", (linear.base() * multiplier) + percent, (linear.perLevelAboveFirst() * multiplier) + percent);
		} else if (value instanceof EnchantmentLevelBasedValue.LevelsSquared levelsSquared) {
			return LText.translatable("emi_loot.enchantment_value.level_squared", (levelsSquared.added() * multiplier) + percent);
		} else if (value instanceof EnchantmentLevelBasedValue.Fraction fraction) {
			return LText.translatable("emi_loot.enchantment_value.fraction", EnchantmentLevelBasedValueParser.parseValue(fraction.numerator(), percent, multiplier), EnchantmentLevelBasedValueParser.parseValue(fraction.denominator(), percent, multiplier));
		} else if (value instanceof EnchantmentLevelBasedValue.Clamped clamped) {
			return LText.translatable("emi_loot.enchantment_value.clamped", EnchantmentLevelBasedValueParser.parseValue(clamped.value(), percent, multiplier), (clamped.min() * multiplier) + percent, (clamped.max() * multiplier) + percent);
		}
		if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Unknown or unparsable enchantment-based value in table: "  + LootTableParser.currentTable);
		return LText.translatable("emi_loot.enchantment_value.level_squared");
	}
}