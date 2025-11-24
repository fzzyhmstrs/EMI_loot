package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.component.ComponentChanges;
import net.minecraft.loot.function.SetComponentsLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetComponentsLootFunction.class)
public interface SetComponentsLootFunctionAccessor {
	@Accessor
	ComponentChanges getChanges();
}