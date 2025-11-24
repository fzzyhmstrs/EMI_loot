package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.SetCustomDataLootFunction;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetCustomDataLootFunction.class)
public interface SetCustomDataLootFunctionAccessor {
	@Accessor
	NbtCompound getNbt();
}