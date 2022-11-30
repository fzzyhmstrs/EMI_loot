package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.SlimePredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SlimePredicate.class)
public interface SlimePredicateAccessor {

    @Accessor(value = "size")
    NumberRange.IntRange getSize();

}
