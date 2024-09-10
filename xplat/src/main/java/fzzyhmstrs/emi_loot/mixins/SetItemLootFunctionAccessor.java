package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.item.Item;
import net.minecraft.loot.function.SetItemLootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetItemLootFunction.class)
public interface SetItemLootFunctionAccessor {
	@Accessor
	RegistryEntry<Item> getItem();
}