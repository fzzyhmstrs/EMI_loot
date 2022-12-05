package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.EntityEquipmentPredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;

public class EntityEquipmentPredicateParser{

    public static Text parseEntityEquipmentPredicate(EntityEquipmentPredicate predicate){
        ItemPredicate head = ((EntityEquipmentPredicateAccessor)predicate).getHead();
        if (!head.equals(ItemPredicate.ANY)){
            return ItemPredicateParser.parseItemPredicate(head);
        }

        ItemPredicate chest = ((EntityEquipmentPredicateAccessor)predicate).getChest();
        if (!chest.equals(ItemPredicate.ANY)){
            return ItemPredicateParser.parseItemPredicate(chest);
        }

        ItemPredicate legs = ((EntityEquipmentPredicateAccessor)predicate).getLegs();
        if (!legs.equals(ItemPredicate.ANY)){
            return ItemPredicateParser.parseItemPredicate(legs);
        }

        ItemPredicate feet = ((EntityEquipmentPredicateAccessor)predicate).getFeet();
        if (!feet.equals(ItemPredicate.ANY)){
            return ItemPredicateParser.parseItemPredicate(feet);
        }

        ItemPredicate mainhand = ((EntityEquipmentPredicateAccessor)predicate).getMainhand();
        if (!mainhand.equals(ItemPredicate.ANY)){
            return ItemPredicateParser.parseItemPredicate(mainhand);
        }

        ItemPredicate offhand = ((EntityEquipmentPredicateAccessor)predicate).getOffhand();
        if (!offhand.equals(ItemPredicate.ANY)){
            return ItemPredicateParser.parseItemPredicate(offhand);
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable equipment predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }
}
