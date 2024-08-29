package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.entity.EntitySubPredicateTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntitySubPredicateTypes.VariantType.VariantPredicate.class)
public interface VariantPredicateAccessor<V> {
	@Accessor
	V getVariant();
}