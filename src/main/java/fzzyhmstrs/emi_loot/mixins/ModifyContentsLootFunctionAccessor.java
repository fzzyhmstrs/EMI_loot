package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.ModifyContentsLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModifyContentsLootFunction.class)
public interface ModifyContentsLootFunctionAccessor {
	@Accessor
	LootFunction getModifier();
}