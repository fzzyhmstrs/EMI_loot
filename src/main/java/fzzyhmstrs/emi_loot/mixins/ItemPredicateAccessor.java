package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.item.Item;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ItemPredicate.class)
public interface ItemPredicateAccessor {

    @Accessor(value = "tag")
    TagKey<Item> getTag();
    @Accessor(value = "items")
    Set<Item> getItems();
    @Accessor(value = "count")
    NumberRange.IntRange getCount();
    @Accessor(value = "durability")
    NumberRange.IntRange getDurability();
    @Accessor(value = "enchantments")
    EnchantmentPredicate[] getEnchantments();
    @Accessor(value = "storedEnchantments")
    EnchantmentPredicate[] getStoredEnchantments();

}
