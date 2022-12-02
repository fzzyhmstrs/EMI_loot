package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.number.LootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BoundedIntUnaryOperator.class)
public interface BoundedIntUnaryOperatorAccessor {

    @Accessor(value = "min")
    LootNumberProvider getMin();

    @Accessor(value = "max")
    LootNumberProvider getMax();

}
