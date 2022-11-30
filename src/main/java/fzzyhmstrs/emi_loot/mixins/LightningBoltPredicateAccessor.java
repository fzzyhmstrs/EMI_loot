package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LightningBoltPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LightningBoltPredicate.class)
public interface LightningBoltPredicateAccessor {

    @Accessor(value = "blocksSetOnFire")
    NumberRange.IntRange getBlocksSetOnFire();

    @Accessor(value = "entityStruck")
    EntityPredicate getEntityStruck();

}
