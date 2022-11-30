package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RandomChanceWithLootingLootCondition.class)
public interface RandomChanceWithLootingLootConditionAccessor {

    @Accessor(value = "chance")
    float getChance();

    @Accessor(value = "lootingMultiplier")
    float getLootingMultiplier();

}
