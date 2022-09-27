package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.condition.RandomChanceLootCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RandomChanceLootCondition.class)
public interface RandomChanceLootConditionAccessor {

    @Accessor(value = "chance")
    float getChance();

}
