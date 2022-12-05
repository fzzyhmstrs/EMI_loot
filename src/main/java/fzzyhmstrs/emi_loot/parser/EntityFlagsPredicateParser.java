package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.EntityFlagsPredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.text.Text;

public class EntityFlagsPredicateParser{


    public static Text parseEntityFlagsPredicate(EntityFlagsPredicate predicate){
        return LText.translatable("emi_loot.entity_predicate.flag",parseEntityFlagsPredicateInternal(predicate).getString());
    }

    private static Text parseEntityFlagsPredicateInternal(EntityFlagsPredicate predicate){
        Boolean isOnFire = ((EntityFlagsPredicateAccessor)predicate).getIsOnFire();
        if (isOnFire != null){
            if (isOnFire){
                return LText.translatable("emi_loot.entity_predicate.fire_true");
            } else {
                return LText.translatable("emi_loot.entity_predicate.fire_false");
            }
        }

        Boolean isSneaking = ((EntityFlagsPredicateAccessor)predicate).getIsSneaking();
        if (isSneaking != null){
            if (isSneaking){
                return LText.translatable("emi_loot.entity_predicate.sneak_true");
            } else {
                return LText.translatable("emi_loot.entity_predicate.sneak_false");
            }
        }

        Boolean isSprinting = ((EntityFlagsPredicateAccessor)predicate).getIsSprinting();
        if (isSprinting != null){
            if (isSprinting){
                return LText.translatable("emi_loot.entity_predicate.sprint_true");
            } else {
                return LText.translatable("emi_loot.entity_predicate.sprint_false");
            }
        }

        Boolean isSwimming = ((EntityFlagsPredicateAccessor)predicate).getIsSwimming();
        if (isSwimming != null){
            if (isSwimming){
                return LText.translatable("emi_loot.entity_predicate.swim_true");
            } else {
                return LText.translatable("emi_loot.entity_predicate.swim_false");
            }
        }

        Boolean isBaby = ((EntityFlagsPredicateAccessor)predicate).getIsBaby();
        if (isBaby != null){
            if (isBaby){
                return LText.translatable("emi_loot.entity_predicate.baby_true");
            } else {
                return LText.translatable("emi_loot.entity_predicate.baby_false");
            }
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable entity flags predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}
