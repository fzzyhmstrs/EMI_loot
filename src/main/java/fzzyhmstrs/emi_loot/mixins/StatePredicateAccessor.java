package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.StatePredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StatePredicate.class)
public interface StatePredicateAccessor {

    @Accessor(value = "conditions")
    List<StatePredicate.Condition> getConditions();

}
