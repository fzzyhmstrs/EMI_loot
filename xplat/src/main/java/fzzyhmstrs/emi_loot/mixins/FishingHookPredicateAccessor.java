package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.entity.FishingHookPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FishingHookPredicate.class)
public interface FishingHookPredicateAccessor {

    @Accessor(value = "inOpenWater")
    boolean getInOpenWater();

}
