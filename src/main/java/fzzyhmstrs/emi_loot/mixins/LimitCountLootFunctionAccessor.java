package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.LimitCountLootFunction;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LimitCountLootFunction.class)
public interface LimitCountLootFunctionAccessor {

    @Accessor(value = "limit")
    BoundedIntUnaryOperator getLimit();

}