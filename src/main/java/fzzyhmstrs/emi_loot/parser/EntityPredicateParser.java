package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.mixins.*;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.*;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public class EntityPredicateParser {

    public static Text parseEntityPredicate(EntityPredicate predicate){
        return LText.translatable("emi_loot.entity_predicate.base",parseEntityPredicateInternal(predicate).getString());
    }

    private static Text parseEntityPredicateInternal(EntityPredicate predicate){
        if (predicate.equals(EntityPredicate.ANY)) return LText.translatable("emi_loot.entity_predicate.any");

        //entity type check
        EntityTypePredicate typePredicate = ((EntityPredicateAccessor)predicate).getType();
        if (!typePredicate.equals(EntityTypePredicate.ANY)) {
            String jsonString = typePredicate.toJson().getAsString();
            if (jsonString.startsWith("#")) {
                return Text.translatable("emi_loot.entity_predicate.type_tag", jsonString);
            } else {
                EntityType<?> type = Registry.ENTITY_TYPE.get(new Identifier(jsonString));
                return Text.translatable("emi_loot.mob_type_predicate.type", type.getName().getString());
            }
        }

        //distance check
        DistancePredicate distancePredicate = ((EntityPredicateAccessor)predicate).getDistance();
        if (!distancePredicate.equals(DistancePredicate.ANY)){
            NumberRange.FloatRange abs = ((DistancePredicateAccessor)distancePredicate).getAbsolute();
            if (!abs.equals(NumberRange.FloatRange.ANY)){
                return LText.translatable("emi_loot.entity_predicate.distance_abs",abs.getMin(),abs.getMax());
            }
            NumberRange.FloatRange hor = ((DistancePredicateAccessor)distancePredicate).getHorizontal();
            if (!hor.equals(NumberRange.FloatRange.ANY)){
                return LText.translatable("emi_loot.entity_predicate.distance_hor",hor.getMin(),hor.getMax());
            }
            NumberRange.FloatRange x = ((DistancePredicateAccessor)distancePredicate).getX();
            if (!x.equals(NumberRange.FloatRange.ANY)){
                return LText.translatable("emi_loot.entity_predicate.distance_x",x.getMin(),x.getMax());
            }
            NumberRange.FloatRange y = ((DistancePredicateAccessor)distancePredicate).getY();
            if (!y.equals(NumberRange.FloatRange.ANY)){
                return LText.translatable("emi_loot.entity_predicate.distance_y",y.getMin(),y.getMax());
            }
            NumberRange.FloatRange z = ((DistancePredicateAccessor)distancePredicate).getZ();
            if (!z.equals(NumberRange.FloatRange.ANY)){
                return LText.translatable("emi_loot.entity_predicate.distance_z",z.getMin(),z.getMax());
            }
        }

        //location check
        LocationPredicate locationPredicate = ((EntityPredicateAccessor)predicate).getLocation();
        if (!locationPredicate.equals(LocationPredicate.ANY)){
            return LocationPredicateParser.parseLocationPredicate(locationPredicate);
        }

        //stepping on check
        LocationPredicate steppingOnPredicate = ((EntityPredicateAccessor)predicate).getSteppingOn();
        if (!steppingOnPredicate.equals(LocationPredicate.ANY)){
            return LocationPredicateParser.parseLocationPredicate(locationPredicate);
        }

        //effects check
        EntityEffectPredicate entityEffectPredicate = ((EntityPredicateAccessor)predicate).getEffects();
        if (!entityEffectPredicate.equals(EntityEffectPredicate.EMPTY)){
            Map<StatusEffect, EntityEffectPredicate.EffectData> effects = ((EntityEffectPredicateAccessor)entityEffectPredicate).getEffects();
        }

        //nbt check
        NbtPredicate nbt = ((EntityPredicateAccessor)predicate).getNbt();
        if (!nbt.equals(NbtPredicate.ANY)){
            return NbtPredicateParser.parseNbtPredicate(nbt);
        }

        //flags check
        EntityFlagsPredicate entityFlagsPredicate = ((EntityPredicateAccessor)predicate).getFlags();
        if (!entityFlagsPredicate.equals(EntityFlagsPredicate.ANY)){
            Boolean isOnFire = ((EntityFlagsPredicateAccessor)entityFlagsPredicate).getIsOnFire();
            if (isOnFire != null){
                if (isOnFire){
                    return LText.translatable("emi_loot.entity_predicate.fire_true");
                } else {
                    return LText.translatable("emi_loot.entity_predicate.fire_false");
                }
            }

            Boolean isSneaking = ((EntityFlagsPredicateAccessor)entityFlagsPredicate).getIsSneaking();
            if (isSneaking != null){
                if (isSneaking){
                    return LText.translatable("emi_loot.entity_predicate.sneak_true");
                } else {
                    return LText.translatable("emi_loot.entity_predicate.sneak_false");
                }
            }

            Boolean isSprinting = ((EntityFlagsPredicateAccessor)entityFlagsPredicate).getIsSprinting();
            if (isSprinting != null){
                if (isSprinting){
                    return LText.translatable("emi_loot.entity_predicate.sprint_true");
                } else {
                    return LText.translatable("emi_loot.entity_predicate.sprint_false");
                }
            }

            Boolean isSwimming = ((EntityFlagsPredicateAccessor)entityFlagsPredicate).getIsSwimming();
            if (isSwimming != null){
                if (isSwimming){
                    return LText.translatable("emi_loot.entity_predicate.swim_true");
                } else {
                    return LText.translatable("emi_loot.entity_predicate.swim_false");
                }
            }

            Boolean isBaby = ((EntityFlagsPredicateAccessor)entityFlagsPredicate).getIsBaby();
            if (isBaby != null){
                if (isBaby){
                    return LText.translatable("emi_loot.entity_predicate.baby_true");
                } else {
                    return LText.translatable("emi_loot.entity_predicate.baby_false");
                }
            }
        }

        //equipment check
        EntityEquipmentPredicate entityEquipmentPredicate = ((EntityPredicateAccessor)predicate).getEquipment();
        if (!entityEquipmentPredicate.equals(EntityEquipmentPredicate.ANY)){
            ItemPredicate head = ((EntityEquipmentPredicateAccessor)entityEquipmentPredicate).getHead();
            if (!head.equals(ItemPredicate.ANY)){
                return ItemPredicateParser.parseItemPredicate(head);
            }

            ItemPredicate chest = ((EntityEquipmentPredicateAccessor)entityEquipmentPredicate).getChest();
            if (!chest.equals(ItemPredicate.ANY)){
                return ItemPredicateParser.parseItemPredicate(chest);
            }

            ItemPredicate legs = ((EntityEquipmentPredicateAccessor)entityEquipmentPredicate).getLegs();
            if (!legs.equals(ItemPredicate.ANY)){
                return ItemPredicateParser.parseItemPredicate(legs);
            }

            ItemPredicate feet = ((EntityEquipmentPredicateAccessor)entityEquipmentPredicate).getFeet();
            if (!feet.equals(ItemPredicate.ANY)){
                return ItemPredicateParser.parseItemPredicate(feet);
            }

            ItemPredicate mainhand = ((EntityEquipmentPredicateAccessor)entityEquipmentPredicate).getMainhand();
            if (!mainhand.equals(ItemPredicate.ANY)){
                return ItemPredicateParser.parseItemPredicate(mainhand);
            }

            ItemPredicate offhand = ((EntityEquipmentPredicateAccessor)entityEquipmentPredicate).getOffhand();
            if (!offhand.equals(ItemPredicate.ANY)){
                return ItemPredicateParser.parseItemPredicate(offhand);
            }
        }

        //Type Specific checks
        TypeSpecificPredicate typeSpecificPredicate = ((EntityPredicateAccessor)predicate).getTypeSpecific();
        if (!typeSpecificPredicate.equals(TypeSpecificPredicate.ANY)){
            return TypeSpecificPredicateParser.parseTypeSpecificPredicate(typeSpecificPredicate);
        }

        //vehicle checks
        EntityPredicate vehicle = ((EntityPredicateAccessor)predicate).getVehicle();
        if (!vehicle.equals(EntityPredicate.ANY)){
            return EntityPredicateParser.parseEntityPredicate(vehicle);
        }

        //passenger checks
        EntityPredicate passenger = ((EntityPredicateAccessor)predicate).getPassenger();
        if (!passenger.equals(EntityPredicate.ANY)){
            return EntityPredicateParser.parseEntityPredicate(passenger);
        }

        //targeted entity checks
        EntityPredicate targetedEntity = ((EntityPredicateAccessor)predicate).getTargetedEntity();
        if (!targetedEntity.equals(EntityPredicate.ANY)){
            return EntityPredicateParser.parseEntityPredicate(targetedEntity);
        }

        return LText.translatable("emi_loot.entity_predicate.any");

    }

}
