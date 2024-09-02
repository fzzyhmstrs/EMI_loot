package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.ReferenceLootFunction;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ReferenceLootFunction.class)
public interface ReferenceLootFunctionAccessor {
	@Accessor
	Identifier getName();
}