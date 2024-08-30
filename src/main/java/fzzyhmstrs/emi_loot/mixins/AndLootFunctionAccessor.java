package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.AndLootFunction;
import net.minecraft.loot.function.LootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(AndLootFunction.class)
public interface AndLootFunctionAccessor {
	@Accessor
	List<LootFunction> getTerms();
}