package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DamageSourcePredicate.class)
public interface DamageSourcePredicateAccessor {

    @Accessor(value = "isProjectile")
    Boolean getIsProjectile();
    @Accessor(value = "isExplosion")
    Boolean getIsExplosion();
    @Accessor(value = "bypassesArmor")
    Boolean getBypassesArmor();
    @Accessor(value = "bypassesInvulnerability")
    Boolean getBypassesInvulnerability();
    @Accessor(value = "bypassesMagic")
    Boolean getBypassesMagic();
    @Accessor(value = "isFire")
    Boolean getIsFire();
    @Accessor(value = "isMagic")
    Boolean getIsMagic();
    @Accessor(value = "isLightning")
    Boolean getIsLightning();
    @Accessor(value = "directEntity")
    EntityPredicate getDirectEntity();
    @Accessor(value = "sourceEntity")
    EntityPredicate getSourceEntity();

}
