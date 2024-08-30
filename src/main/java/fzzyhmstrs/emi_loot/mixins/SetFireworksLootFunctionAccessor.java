package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.component.type.FireworksComponent;
import net.minecraft.loot.function.SetFireworksLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SetFireworksLootFunction.class)
public interface SetFireworksLootFunctionAccessor {
	@Invoker
	FireworksComponent callApply(FireworksComponent fireworksComponent);
}