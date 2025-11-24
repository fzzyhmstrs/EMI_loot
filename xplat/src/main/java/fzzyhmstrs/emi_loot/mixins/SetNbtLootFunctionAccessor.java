package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetNbtLootFunction.class)
public interface SetNbtLootFunctionAccessor {
	@Accessor
	NbtCompound getNbt();
}