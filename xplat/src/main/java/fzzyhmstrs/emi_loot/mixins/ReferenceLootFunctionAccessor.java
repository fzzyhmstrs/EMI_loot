package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.ReferenceLootFunction;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ReferenceLootFunction.class)
public interface ReferenceLootFunctionAccessor {
	@Accessor
	RegistryKey<LootFunction> getName();
}