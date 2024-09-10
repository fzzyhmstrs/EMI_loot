package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ApplyBonusLootFunction.class)
public interface ApplyBonusLootFunctionAccessor {

    @Accessor(value = "enchantment")
    RegistryEntry<Enchantment> getEnchantment();
}
