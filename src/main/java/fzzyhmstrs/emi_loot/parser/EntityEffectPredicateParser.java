package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;

public class EntityEffectPredicateParser{

    public static Text parseEntityEffectPredicate(EntityEffectPredicate predicate){
        Map<RegistryEntry<StatusEffect>, EntityEffectPredicate.EffectData> effects = predicate.effects();
        List<MutableText> list = new LinkedList<>();
        for (Map.Entry<RegistryEntry<StatusEffect>, EntityEffectPredicate.EffectData> entry : effects.entrySet()) {
            Text name = entry.getKey().value().getName();
            EntityEffectPredicate.EffectData data = entry.getValue();
            NumberRange.IntRange amplifier = data.amplifier();
            if (!amplifier.equals(NumberRange.IntRange.ANY)){
                Optional<Integer> min = amplifier.min();
                Optional<Integer> max = amplifier.max();
                if (Objects.equals(min, max) && min.isPresent()){
                    list.add(LText.translatable("emi_loot.entity_predicate.effect.amplifier", name.getString(), min.get() + 1));
                } else if (min.isPresent() && max.isPresent()) {
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.amplifier_2", name.getString(), min.get() + 1, max.get() + 1));
                } else {
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.amplifier_3", name.getString()));
                }
            }

            NumberRange.IntRange duration = data.duration();
            if (!duration.equals(NumberRange.IntRange.ANY)){
                Optional<Integer> min = duration.min();
                Optional<Integer> max = duration.max();
                if (Objects.equals(min, max) && min.isPresent()){
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.duration", name.getString(), min.get() + 1));
                } else if (min.isPresent() && max.isPresent()) {
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.duration_2", name.getString(), min.get() + 1, max.get() + 1));
                } else {
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.duration_3", name.getString()));
                }
            }

            Optional<Boolean> ambient = data.ambient();
            if (ambient.isPresent()){
                if (ambient.get()){
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.ambient_true", name.getString()));
                } else {
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.ambient_false", name.getString()));
                }
            }

            Optional<Boolean> visible = data.visible();
            if (visible.isPresent()){
                if (visible.get()){
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.visible_true", name.getString()));
                } else {
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.visible_false", name.getString()));
                }
            }
            list.add(LText.translatable("emi_loot.entity_predicate.effect.fallback", name.getString()));
        
        }
        if (!list.isEmpty()){
            return LText.translatable("emi_loot.entity_predicate.effect_1", ListProcessors.buildAndList(list));
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Unparsable entity effect predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }
}
