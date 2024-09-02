package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.entity.EntityFlagsPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityFlagsPredicate.class)
public interface EntityFlagsPredicateAccessor {

    @Accessor(value = "isOnFire")
    Boolean getIsOnFire();

    @Accessor(value = "isSneaking")
    Boolean getIsSneaking();

    @Accessor(value = "isSprinting")
    Boolean getIsSprinting();

    @Accessor(value = "isSwimming")
    Boolean getIsSwimming();

    @Accessor(value = "isBaby")
    Boolean getIsBaby();
}
