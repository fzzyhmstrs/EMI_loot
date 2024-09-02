package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.predicate.item.EnchantmentPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnchantmentPredicate.class)
public interface EnchantmentPredicateAccessor {

    @Accessor(value = "enchantment")
    Enchantment getEnchantment();

}
