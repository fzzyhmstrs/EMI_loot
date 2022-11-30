package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityEffectPredicate.EffectData.class)
public interface EffectDataAccessor {

    @Accessor(value = "amplifier")
    NumberRange.IntRange getAmplifier();

    @Accessor(value = "duration")
    NumberRange.IntRange getDuration();

    @Accessor(value = "ambient")
    Boolean getAmbient();

    @Accessor(value = "visible")
    Boolean getVisible();
}
