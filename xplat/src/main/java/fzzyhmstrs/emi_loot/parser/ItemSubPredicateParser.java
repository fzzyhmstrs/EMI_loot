package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.EnchantmentsPredicateAccessor;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.AttributeModifiersPredicate;
import net.minecraft.predicate.item.BundleContentsPredicate;
import net.minecraft.predicate.item.ContainerPredicate;
import net.minecraft.predicate.item.CustomDataPredicate;
import net.minecraft.predicate.item.DamagePredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.predicate.item.FireworkExplosionPredicate;
import net.minecraft.predicate.item.FireworksPredicate;
import net.minecraft.predicate.item.ItemSubPredicate;
import net.minecraft.predicate.item.PotionContentsPredicate;
import net.minecraft.predicate.item.TrimPredicate;
import net.minecraft.predicate.item.WritableBookContentPredicate;
import net.minecraft.predicate.item.WrittenBookContentPredicate;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class ItemSubPredicateParser {

    public static Text parseItemSubPredicate(ItemSubPredicate.Type<? extends ItemSubPredicate> type, ItemSubPredicate predicate) {
        if (predicate instanceof DamagePredicate damagePredicate) {
            NumberRange.IntRange durability = damagePredicate.durability();
            if (durability != NumberRange.IntRange.ANY) {
                int finalMax = durability.max().orElse(0);
                int finalMin = durability.min().orElse(0);
                return LText.translatable("emi_loot.item_predicate.durability", Integer.toString(finalMin), Integer.toString(finalMax));
            }
            NumberRange.IntRange damage = damagePredicate.damage();
            if (damage != NumberRange.IntRange.ANY) {
                int finalMax = durability.max().orElse(0);
                int finalMin = durability.min().orElse(0);
                return LText.translatable("emi_loot.item_predicate.damage", Integer.toString(finalMin), Integer.toString(finalMax));
            }
        } else if (predicate instanceof EnchantmentsPredicate.Enchantments enchantmentPredicate) {
            return EnchantmentPredicateParser.parseEnchantmentPredicates(((EnchantmentsPredicateAccessor)enchantmentPredicate).getEnchantments());
        } else if (predicate instanceof EnchantmentsPredicate.StoredEnchantments enchantmentPredicate) {
            return EnchantmentPredicateParser.parseEnchantmentPredicates(((EnchantmentsPredicateAccessor)enchantmentPredicate).getEnchantments());
        } else if (predicate instanceof PotionContentsPredicate potionContentsPredicate) {
            RegistryEntryList<Potion> potions = potionContentsPredicate.potions();
            if (potions.getTagKey().isPresent()) {
                return LText.translatable("emi_loot.predicate.potion.tag", potions.getTagKey().get().id().toString());
            }
            if (potions.size() > 0) {
                List<MutableText> list = potions.stream().map(p -> LText.translatable(Potion.finishTranslationKey(Optional.of(p), Items.POTION.getTranslationKey() + ".effect."))).toList();
                return LText.translatable("emi_loot.predicate.potion.list", ListProcessors.buildOrList(list));
            }
        } else if (predicate instanceof CustomDataPredicate customDataPredicate) {
            return NbtPredicateParser.parseNbtPredicate(customDataPredicate.value());
        } else if (predicate instanceof ContainerPredicate) {
            return LText.translatable("emi_loot.predicate.container");
        } else if (predicate instanceof BundleContentsPredicate) {
            return LText.translatable("emi_loot.predicate.container");
        } else if (predicate instanceof FireworkExplosionPredicate fireworkExplosionPredicate) {
            FireworkExplosionPredicate.Predicate fireworkExplosionPredicatePredicate = fireworkExplosionPredicate.predicate();
            if (fireworkExplosionPredicatePredicate.shape().isPresent()) {
                Text v = fireworkExplosionPredicatePredicate.shape().get().getName();
                return LText.translatable("emi_loot.predicate.firework.shape", v);
            }
            if (fireworkExplosionPredicatePredicate.twinkle().isPresent()) {
                return fireworkExplosionPredicatePredicate.twinkle().get() ? LText.translatable("emi_loot.predicate.firework.twinkle") :LText.translatable("emi_loot.predicate.firework.no_twinkle");
            }
            if (fireworkExplosionPredicatePredicate.trail().isPresent()) {
                return fireworkExplosionPredicatePredicate.trail().get() ? LText.translatable("emi_loot.predicate.firework.trail") :LText.translatable("emi_loot.predicate.firework.no_trail");
            }
            return LText.translatable("emi_loot.predicate.container");
        } else if (predicate instanceof FireworksPredicate) {
            return LText.translatable("emi_loot.predicate.fireworks");
        } else if (predicate instanceof WritableBookContentPredicate) {
            return LText.translatable("emi_loot.predicate.writable_book");
        } else if (predicate instanceof WrittenBookContentPredicate) {
            return LText.translatable("emi_loot.predicate.writable_book");
        } else if (predicate instanceof AttributeModifiersPredicate) {
            return LText.translatable("emi_loot.predicate.attribute");
        } else if (predicate instanceof TrimPredicate) {
            return LText.translatable("emi_loot.predicate.trim");
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty item predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}