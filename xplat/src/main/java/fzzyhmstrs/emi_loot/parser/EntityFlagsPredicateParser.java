package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.text.Text;

import java.util.Optional;

public class EntityFlagsPredicateParser {


    public static Text parseEntityFlagsPredicate(EntityFlagsPredicate predicate) {
        return LText.translatable("emi_loot.entity_predicate.flag", parseEntityFlagsPredicateInternal(predicate).getString());
    }

    private static Text parseEntityFlagsPredicateInternal(EntityFlagsPredicate predicate) {
        Optional<Boolean> isOnFire = predicate.isOnFire();
        if (isOnFire.isPresent()) {
            if (isOnFire.get()) {
                return LText.translatable("emi_loot.entity_predicate.fire_true");
            } else {
                return LText.translatable("emi_loot.entity_predicate.fire_false");
            }
        }

        Optional<Boolean> isSneaking = predicate.isSneaking();
        if (isSneaking.isPresent()) {
            if (isSneaking.get()) {
                return LText.translatable("emi_loot.entity_predicate.sneak_true");
            } else {
                return LText.translatable("emi_loot.entity_predicate.sneak_false");
            }
        }

        Optional<Boolean> isSprinting = predicate.isSprinting();
        if (isSprinting.isPresent()) {
            if (isSprinting.get()) {
                return LText.translatable("emi_loot.entity_predicate.sprint_true");
            } else {
                return LText.translatable("emi_loot.entity_predicate.sprint_false");
            }
        }

        Optional<Boolean> isSwimming = predicate.isSwimming();
        if (isSwimming.isPresent()) {
            if (isSwimming.get()) {
                return LText.translatable("emi_loot.entity_predicate.swim_true");
            } else {
                return LText.translatable("emi_loot.entity_predicate.swim_false");
            }
        }

        Optional<Boolean> isBaby = predicate.isBaby();
        if (isBaby.isPresent()) {
            if (isBaby.get()) {
                return LText.translatable("emi_loot.entity_predicate.baby_true");
            } else {
                return LText.translatable("emi_loot.entity_predicate.baby_false");
            }
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable entity flags predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}