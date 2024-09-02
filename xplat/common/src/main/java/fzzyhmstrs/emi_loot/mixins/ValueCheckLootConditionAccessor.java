package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.condition.ValueCheckLootCondition;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.number.LootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ValueCheckLootCondition.class)
public interface ValueCheckLootConditionAccessor {

    @Accessor(value = "value")
    LootNumberProvider getValue();

    @Accessor(value = "range")
    BoundedIntUnaryOperator getRange();

}
