package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DamageSourcePredicate.class)
public interface DamageSourcePredicateAccessor {
    @Accessor(value = "directEntity")
    EntityPredicate getDirectEntity();
    @Accessor(value = "sourceEntity")
    EntityPredicate getSourceEntity();

}
