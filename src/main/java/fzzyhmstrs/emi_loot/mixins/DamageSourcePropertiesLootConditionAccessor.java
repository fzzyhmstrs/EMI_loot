package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DamageSourcePropertiesLootCondition.class)
public interface DamageSourcePropertiesLootConditionAccessor {

    @Accessor(value = "predicate")
    DamageSourcePredicate getPredicate();

}
