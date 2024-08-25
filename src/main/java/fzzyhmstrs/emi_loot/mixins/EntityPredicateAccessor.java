package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPredicate.class)
public interface EntityPredicateAccessor {

    @Accessor(value = "type")
    EntityTypePredicate getType();

    @Accessor(value = "distance")
    DistancePredicate getDistance();

    @Accessor(value = "location")
    LocationPredicate getLocation();

    @Accessor(value = "steppingOn")
    LocationPredicate getSteppingOn();

    @Accessor(value = "effects")
    EntityEffectPredicate getEffects();

    @Accessor(value = "nbt")
    NbtPredicate getNbt();

    @Accessor(value = "flags")
    EntityFlagsPredicate getFlags();

    @Accessor(value = "equipment")
    EntityEquipmentPredicate getEquipment();

    @Accessor(value = "typeSpecific")
    TypeSpecificPredicate getTypeSpecific();

    @Accessor(value = "vehicle")
    EntityPredicate getVehicle();

    @Accessor(value = "passenger")
    EntityPredicate getPassenger();

    @Accessor(value = "targetedEntity")
    EntityPredicate getTargetedEntity();

    @Accessor(value = "team")
    String getTeam();
}