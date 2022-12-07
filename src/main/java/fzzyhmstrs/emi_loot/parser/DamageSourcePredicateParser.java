package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.DamageSourcePredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.text.Text;

public class DamageSourcePredicateParser {

    public static Text parseDamageSourcePredicate(DamageSourcePredicate predicate){

        Boolean isProjectile = ((DamageSourcePredicateAccessor)predicate).getIsProjectile();
        if (isProjectile != null){
            if (isProjectile){
                return LText.translatable("emi_loot.damage_predicate.projectile_true");
            } else {
                return LText.translatable("emi_loot.damage_predicate.projectile_false");
            }
        }

        Boolean isExplosion = ((DamageSourcePredicateAccessor)predicate).getIsExplosion();
        if (isExplosion != null){
            if (isExplosion){
                return LText.translatable("emi_loot.damage_predicate.explosion_true");
            } else {
                return LText.translatable("emi_loot.damage_predicate.explosion_false");
            }
        }

        Boolean bypassesArmor = ((DamageSourcePredicateAccessor)predicate).getBypassesArmor();
        if (bypassesArmor != null){
            if (bypassesArmor){
                return LText.translatable("emi_loot.damage_predicate.bypass_armor_true");
            } else {
                return LText.translatable("emi_loot.damage_predicate.bypass_armor_false");
            }
        }

        Boolean bypassesInvulnerability = ((DamageSourcePredicateAccessor)predicate).getBypassesInvulnerability();
        if (bypassesInvulnerability != null){
            if (bypassesInvulnerability){
                return LText.translatable("emi_loot.damage_predicate.no_invulnerable_true");
            } else {
                return LText.translatable("emi_loot.damage_predicate.no_invulnerable_false");
            }
        }

        Boolean bypassesMagic = ((DamageSourcePredicateAccessor)predicate).getBypassesMagic();
        if (bypassesMagic != null){
            if (bypassesMagic){
                return LText.translatable("emi_loot.damage_predicate.unblockable_true");
            } else {
                return LText.translatable("emi_loot.damage_predicate.unblockable_false");
            }
        }

        Boolean isFire = ((DamageSourcePredicateAccessor)predicate).getIsFire();
        if (isFire != null){
            if (isFire){
                return LText.translatable("emi_loot.damage_predicate.fire_true");
            } else {
                return LText.translatable("emi_loot.damage_predicate.fire_false");
            }
        }

        Boolean isMagic = ((DamageSourcePredicateAccessor)predicate).getIsMagic();
        if (isMagic != null){
            if (isMagic){
                return LText.translatable("emi_loot.damage_predicate.magic_true");
            } else {
                return LText.translatable("emi_loot.damage_predicate.magic_false");
            }
        }

        Boolean isLightning = ((DamageSourcePredicateAccessor)predicate).getIsLightning();
        if (isLightning != null){
            if (isLightning){
                return LText.translatable("emi_loot.damage_predicate.lightning_true");
            } else {
                return LText.translatable("emi_loot.damage_predicate.lightning_false");
            }
        }

        EntityPredicate directPredicate = ((DamageSourcePredicateAccessor)predicate).getDirectEntity();
        if (!directPredicate.equals(EntityPredicate.ANY)){
            return EntityPredicateParser.parseEntityPredicate(directPredicate);
        }

        EntityPredicate sourcePredicate = ((DamageSourcePredicateAccessor)predicate).getSourceEntity();
        if (!sourcePredicate.equals(EntityPredicate.ANY)){
            return EntityPredicateParser.parseEntityPredicate(sourcePredicate);
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable damage source predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}
