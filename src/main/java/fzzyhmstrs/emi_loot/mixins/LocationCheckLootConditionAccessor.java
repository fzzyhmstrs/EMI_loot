package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.LootCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface LocationCheckLootConditionAccessor{

    @Accessor(value = "predicate")
    LocationPredicate getPredicate();

}
