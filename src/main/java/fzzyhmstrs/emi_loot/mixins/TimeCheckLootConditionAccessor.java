package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.condition.TimeCheckLootCondition;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TimeCheckLootCondition.class)
public interface TimeCheckLootConditionAccessor {

    @Accessor(value = "period")
    Long getPeriod();

    @Accessor(value = "value")
    BoundedIntUnaryOperator getValue();

}
