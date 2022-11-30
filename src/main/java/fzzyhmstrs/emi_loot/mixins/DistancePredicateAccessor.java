package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DistancePredicate.class)
public interface DistancePredicateAccessor {

    @Accessor(value = "absolute")
    NumberRange.FloatRange getAbsolute();

    @Accessor(value = "horizontal")
    NumberRange.FloatRange getHorizontal();

    @Accessor(value = "x")
    NumberRange.FloatRange getX();

    @Accessor(value = "y")
    NumberRange.FloatRange getY();

    @Accessor(value = "z")
    NumberRange.FloatRange getZ();
}
