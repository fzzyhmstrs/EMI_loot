package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;

import java.util.Optional;

public class EntityEquipmentPredicateParser {

    public static Text parseEntityEquipmentPredicate(EntityEquipmentPredicate predicate) {
        Optional<ItemPredicate> head = predicate.head();
        if (head.isPresent()) {
            return ItemPredicateParser.parseItemPredicate(head.get());
        }

        Optional<ItemPredicate> chest = predicate.chest();
        if (chest.isPresent()) {
            return ItemPredicateParser.parseItemPredicate(chest.get());
        }

        Optional<ItemPredicate> legs = predicate.legs();
        if (legs.isPresent()) {
            return ItemPredicateParser.parseItemPredicate(legs.get());
        }

        Optional<ItemPredicate> feet = predicate.feet();
        if (feet.isPresent()) {
            return ItemPredicateParser.parseItemPredicate(feet.get());
        }

        Optional<ItemPredicate> mainhand = predicate.mainhand();
        if (mainhand.isPresent()) {
            return ItemPredicateParser.parseItemPredicate(mainhand.get());
        }

        Optional<ItemPredicate> offhand = predicate.offhand();
        if (offhand.isPresent()) {
            return ItemPredicateParser.parseItemPredicate(offhand.get());
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable equipment predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }
}