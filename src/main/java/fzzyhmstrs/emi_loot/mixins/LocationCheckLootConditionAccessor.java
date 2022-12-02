package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.predicate.entity.LocationPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LocationCheckLootCondition.class)
public interface LocationCheckLootConditionAccessor{

    @Accessor(value = "predicate")
    LocationPredicate getPredicate();

}
