package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(EntityEffectPredicate.class)
public interface EntityEffectPredicateAccessor {

    @Accessor(value = "effects")
    Map<StatusEffect, EntityEffectPredicate.EffectData> getEffects();

}
