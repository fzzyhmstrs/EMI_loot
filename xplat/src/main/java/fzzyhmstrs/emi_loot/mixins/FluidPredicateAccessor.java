package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.fluid.Fluid;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FluidPredicate.class)
public interface FluidPredicateAccessor {

    @Accessor(value = "tag")
    TagKey<Fluid> getTag();

    @Accessor(value = "fluid")
    Fluid getFluid();

    @Accessor(value = "state")
    StatePredicate getState();

}
