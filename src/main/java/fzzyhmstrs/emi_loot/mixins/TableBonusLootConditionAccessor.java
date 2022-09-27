package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.condition.TableBonusLootCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TableBonusLootCondition.class)
public interface TableBonusLootConditionAccessor {

    @Accessor(value = "enchantment")
    Enchantment getEnchantment();
}
