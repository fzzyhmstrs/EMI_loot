package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.mixins.DistancePredicateAccessor;
import fzzyhmstrs.emi_loot.mixins.EntityPredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityPredicateParser {

    public static Text parseEntityPredicate(EntityPredicate predicate){
        if (predicate.equals(EntityPredicate.ANY)) return LText.translatable("emi_loot.entity_predicate.any");

        //entity type check
        EntityTypePredicate typePredicate = ((EntityPredicateAccessor)predicate).getType();
        if (typePredicate != null && typePredicate == EntityTypePredicate.ANY) {
            return LText.translatable("emi_loot.entity_predicate.type_any");
        } else if (typePredicate != null ) {
            String jsonString = typePredicate.toJson().getAsString();
            if (jsonString.startsWith("#")) {
                return Text.translatable("emi_loot.entity_predicate.type_tag", jsonString);
            } else {
                EntityType<?> type = Registry.ENTITY_TYPE.get(new Identifier(jsonString));
                return Text.translatable("emi_loot.mob_type_predicate.type", type.getName().getString());
            }
        }

        //distance check
        DistancePredicate distancePredicate = ((EntityPredicateAccessor)predicate).getDistance();
        if (distancePredicate != null && distancePredicate == DistancePredicate.ANY) {
            return LText.translatable("emi_loot.entity_predicate.distance_any");
        } else if (distancePredicate != null){
            NumberRange.FloatRange abs = ((DistancePredicateAccessor)distancePredicate).getAbsolute();
            if (!abs.equals(NumberRange.FloatRange.ANY)){
                return LText.translatable("emi_loot.entity_predicate.distance_abs",abs.getMin(),abs.getMax());
            }
            NumberRange.FloatRange hor = ((DistancePredicateAccessor)distancePredicate).getHorizontal();
            if (!hor.equals(NumberRange.FloatRange.ANY)){
                return LText.translatable("emi_loot.entity_predicate.distance_hor",hor.getMin(),hor.getMax());
            }
            NumberRange.FloatRange x = ((DistancePredicateAccessor)distancePredicate).getX();
            if (!x.equals(NumberRange.FloatRange.ANY)){
                return LText.translatable("emi_loot.entity_predicate.distance_x",x.getMin(),x.getMax());
            }
            NumberRange.FloatRange y = ((DistancePredicateAccessor)distancePredicate).getY();
            if (!y.equals(NumberRange.FloatRange.ANY)){
                return LText.translatable("emi_loot.entity_predicate.distance_y",y.getMin(),y.getMax());
            }
            NumberRange.FloatRange z = ((DistancePredicateAccessor)distancePredicate).getZ();
            if (!z.equals(NumberRange.FloatRange.ANY)){
                return LText.translatable("emi_loot.entity_predicate.distance_z",z.getMin(),z.getMax());
            }
        }

        //location check

        return LText.empty();
    }

}
