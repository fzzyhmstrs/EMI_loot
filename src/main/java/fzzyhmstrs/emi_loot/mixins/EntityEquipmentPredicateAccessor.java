package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityEquipmentPredicate.class)
public interface EntityEquipmentPredicateAccessor {

    @Accessor(value = "head")
    ItemPredicate getHead();

    @Accessor(value = "chest")
    ItemPredicate getChest();

    @Accessor(value = "legs")
    ItemPredicate getLegs();

    @Accessor(value = "feet")
    ItemPredicate getFeet();

    @Accessor(value = "mainhand")
    ItemPredicate getMainhand();

    @Accessor(value = "offhand")
    ItemPredicate getOffhand();
}
