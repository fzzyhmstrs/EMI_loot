package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.mixins.LightningBoltPredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LightningBoltPredicate;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import net.minecraft.text.Text;

public class TypeSpecificPredicateParser {

    public static Text parseTypeSpecificPredicate(TypeSpecificPredicate predicate){
        if (predicate instanceof LightningBoltPredicate){
            NumberRange.IntRange blocksSetOnFire = ((LightningBoltPredicateAccessor)predicate).getBlocksSetOnFire();
            if (!blocksSetOnFire.equals(NumberRange.IntRange.ANY)){
                return LText.translatable("blah");
            }

            EntityPredicate entityStruck = ((LightningBoltPredicateAccessor)predicate).getEntityStruck();
            if(!entityStruck.equals(EntityPredicate.ANY)){
                return EntityPredicateParser.parseEntityPredicate(entityStruck);
            }

        }

        return LText.translatable("emi_loot.entity_predicate.type_specific.any");
    }


}
