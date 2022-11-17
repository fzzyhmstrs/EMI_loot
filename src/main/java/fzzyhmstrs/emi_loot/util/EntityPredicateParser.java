package fzzyhmstrs.emi_loot.util;

import fzzyhmstrs.emi_loot.mixins.EntityPredicateAccessor;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.text.Text;

public class EntityPredicateParser {

    public static Text parseEntityPredicate(EntityPredicate predicate){
        EntityTypePredicate typePredicate = ((EntityPredicateAccessor)predicate).getTypePredicate();
        if (typePredicate != EntityTypePredicate.ANY) {
            /*if (typePredicate.toJson().getAsString().startsWith("#")) {
                TagKey<EntityType<?>> tag = ((TaggedAccessor) typePredicate).getTag();
                return Text.translatable("emi_loot.mob_type_predicate.tag", tag.id().toString());
            } else {
                EntityType<?> type = ((SingleAccessor) predicate).getType();
                return Text.translatable("emi_loot.mob_type_predicate.type", type.getName().getString());
            }*/
            return LText.translatable("emi_loot.entity_predicate.mob_type");
        }
        return LText.empty();
    }

}
