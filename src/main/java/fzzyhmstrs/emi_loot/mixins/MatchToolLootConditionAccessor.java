package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.predicate.item.ItemPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MatchToolLootCondition.class)
public interface MatchToolLootConditionAccessor {

    @Accessor(value = "predicate")
    ItemPredicate getPredicate();

}
