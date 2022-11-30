package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LightPredicate.class)
public interface LightPredicateAccessor {

    @Accessor(value = "range")
    NumberRange.IntRange getRange();

}
