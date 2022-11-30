package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.mixins.*;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;

public class EntityEquipmentPredicateParser{

    public static Text parseEntityEquipmentPredicate(EntityEquipmentPredicate predicate){
        ItemPredicate head = ((EntityEquipmentPredicateAccessor)entityEquipmentPredicate).getHead();
        if (!head.equals(ItemPredicate.ANY)){
            return ItemPredicateParser.parseItemPredicate(head);
        }

        ItemPredicate chest = ((EntityEquipmentPredicateAccessor)entityEquipmentPredicate).getChest();
        if (!chest.equals(ItemPredicate.ANY)){
            return ItemPredicateParser.parseItemPredicate(chest);
        }

        ItemPredicate legs = ((EntityEquipmentPredicateAccessor)entityEquipmentPredicate).getLegs();
        if (!legs.equals(ItemPredicate.ANY)){
            return ItemPredicateParser.parseItemPredicate(legs);
        }

        ItemPredicate feet = ((EntityEquipmentPredicateAccessor)entityEquipmentPredicate).getFeet();
        if (!feet.equals(ItemPredicate.ANY)){
            return ItemPredicateParser.parseItemPredicate(feet);
        }

        ItemPredicate mainhand = ((EntityEquipmentPredicateAccessor)entityEquipmentPredicate).getMainhand();
        if (!mainhand.equals(ItemPredicate.ANY)){
            return ItemPredicateParser.parseItemPredicate(mainhand);
        }

        ItemPredicate offhand = ((EntityEquipmentPredicateAccessor)entityEquipmentPredicate).getOffhand();
        if (!offhand.equals(ItemPredicate.ANY)){
            return ItemPredicateParser.parseItemPredicate(offhand);
        }
        return LText.empty();
    }
}
