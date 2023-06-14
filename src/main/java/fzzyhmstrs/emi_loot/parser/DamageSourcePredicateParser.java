package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.DamageSourcePredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.text.Text;

public class DamageSourcePredicateParser {

    public static Text parseDamageSourcePredicate(DamageSourcePredicate predicate){
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
