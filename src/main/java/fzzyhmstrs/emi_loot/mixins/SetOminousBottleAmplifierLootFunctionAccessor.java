package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.SetOminousBottleAmplifierLootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetOminousBottleAmplifierLootFunction.class)
public interface SetOminousBottleAmplifierLootFunctionAccessor {
	@Accessor
	LootNumberProvider getAmplifier();
}