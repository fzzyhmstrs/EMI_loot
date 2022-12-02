package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPropertiesLootCondition.class)
public interface EntityPropertiesLootConditionAccessor{

    @Accessor(value = "entity")
    LootContext.EntityTarget getEntity();
    
    @Accessor(value = "predicate")
    EntityPredicate getPredicate();

}
