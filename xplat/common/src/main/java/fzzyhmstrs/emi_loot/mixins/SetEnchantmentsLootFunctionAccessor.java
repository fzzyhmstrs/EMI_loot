package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SetEnchantmentsLootFunction.class)
public interface SetEnchantmentsLootFunctionAccessor {

    @Accessor(value = "enchantments")
    Map<Enchantment, LootNumberProvider> getEnchantments();

    @Accessor(value = "add")
    boolean getAdd();

}
