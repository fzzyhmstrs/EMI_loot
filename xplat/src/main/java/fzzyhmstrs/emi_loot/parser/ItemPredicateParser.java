package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.item.Item;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ItemPredicateParser {

    public static Text parseItemPredicate(ItemPredicate predicate) {
        Optional<TagKey<Item>> tag = predicate.tag();
        if (tag.isPresent()) {
            return LText.translatable("emi_loot.item_predicate.tag",tag.get().id());
        }

        Set<Item> items = ((ItemPredicateAccessor)predicate).getItems();
        if (items != null && !items.isEmpty()) {
            List<MutableText> list = items.stream().map((item) -> (MutableText)item.getName()).toList();
            return LText.translatable("emi_loot.item_predicate.items", ListProcessors.buildOrList(list));
        }

        NumberRange.IntRange count = ((ItemPredicateAccessor)predicate).getCount();
        if (count != NumberRange.IntRange.ANY) {
            int finalMax = count.max().orElse(0);
            int finalMin = count.min().orElse(0);
            return LText.translatable("emi_loot.item_predicate.count", Integer.toString(finalMin), Integer.toString(finalMax));
        }

        NumberRange.IntRange durability = ((ItemPredicateAccessor)predicate).getDurability();
        if (durability != NumberRange.IntRange.ANY) {
            int finalMax = durability.max().orElse(0);
            int finalMin = durability.min().orElse(0);
            return LText.translatable("emi_loot.item_predicate.durability", Integer.toString(finalMin), Integer.toString(finalMax));
        }

        EnchantmentPredicate[] enchants = ((ItemPredicateAccessor)predicate).getEnchantments();
        EnchantmentPredicate[] storedEnchants = ((ItemPredicateAccessor)predicate).getStoredEnchantments();
        if (enchants.length + storedEnchants.length > 0) {
            List<EnchantmentPredicate> list = new ArrayList<>();
            list.addAll(Arrays.stream(enchants).toList());
            list.addAll(Arrays.stream(storedEnchants).toList());
            return EnchantmentPredicateParser.parseEnchantmentPredicates(list);
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty item predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}