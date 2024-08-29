package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(EnchantmentsPredicate.class)
public interface EnchantmentsPredicateAccessor {
	@Accessor
	List<EnchantmentPredicate> getEnchantments();
}