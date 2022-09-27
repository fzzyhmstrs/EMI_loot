package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPredicate.class)
public interface EntityPredicateAccessor {

    @Accessor(value = "type")
    EntityTypePredicate getTypePredicate();

}
