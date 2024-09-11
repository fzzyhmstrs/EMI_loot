package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class EnchantmentPredicateParser {

    public static Text parseEnchantmentPredicates(List<EnchantmentPredicate> list) {
        List<MutableText> list2 = new LinkedList<>();
        for (EnchantmentPredicate predicate : list) {
            Optional<RegistryEntryList<Enchantment>> enchant = predicate.enchantments();
            if (enchant.isPresent() && enchant.get().getTagKey().isPresent()) {
                if (predicate.levels() == NumberRange.IntRange.ANY) {
                    list2.add(LText.translatable("emi_loot.item_predicate.enchant.tag", enchant.get().getTagKey().get().id().toString()));
                } else {
                    Text numberRange = NumberProcessors.processNumberRange(
                            predicate.levels(),
                            "emi_loot.item_predicate.enchant.levels_1",
                            "emi_loot.item_predicate.enchant.levels_2",
                            "emi_loot.item_predicate.enchant.levels_3",
                            "emi_loot.item_predicate.enchant.levels_4",
                            ""
                    );
                    Text e = LText.translatable("emi_loot.item_predicate.enchant.tag", enchant.get().getTagKey().get().id().toString());
                    list2.add(LText.translatable("emi_loot.item_predicate.enchant.levels", e, numberRange));
                }
            }
            if (enchant.isPresent() && enchant.get().size() > 0) {
                List<MutableText> list3 = enchant.get().stream().map(entry -> LText.enchant(entry).copyContentOnly()).toList();
                if (predicate.levels() == NumberRange.IntRange.ANY) {
                    list2.add(LText.translatable("emi_loot.item_predicate.enchant.list", ListProcessors.buildOrList(list3)));
                } else {
                    Text numberRange = NumberProcessors.processNumberRange(
                            predicate.levels(),
                            "emi_loot.item_predicate.enchant.levels_1",
                            "emi_loot.item_predicate.enchant.levels_2",
                            "emi_loot.item_predicate.enchant.levels_3",
                            "emi_loot.item_predicate.enchant.levels_4",
                            ""
                        );
                    Text e = LText.translatable("emi_loot.item_predicate.enchant.list", ListProcessors.buildOrList(list3));
                    list2.add(LText.translatable("emi_loot.item_predicate.enchant.levels", e, numberRange));
                }
            }
        }
        if (!list2.isEmpty()) {
            return LText.translatable("emi_loot.item_predicate.enchant", ListProcessors.buildAndList(list2));
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable enchantment predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }
}