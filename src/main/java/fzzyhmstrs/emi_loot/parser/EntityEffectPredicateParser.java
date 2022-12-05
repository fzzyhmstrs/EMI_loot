package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.EffectDataAccessor;
import fzzyhmstrs.emi_loot.mixins.EntityEffectPredicateAccessor;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntityEffectPredicateParser{

    public static Text parseEntityEffectPredicate(EntityEffectPredicate predicate){
        Map<StatusEffect, EntityEffectPredicate.EffectData> effects = ((EntityEffectPredicateAccessor)predicate).getEffects();
        List<MutableText> list = new LinkedList<>();
        for (Map.Entry<StatusEffect, EntityEffectPredicate.EffectData> entry : effects.entrySet()) {
            Text name = entry.getKey().getName();
            EntityEffectPredicate.EffectData data = entry.getValue();
            NumberRange.IntRange amplifier = ((EffectDataAccessor)data).getAmplifier();
            if (!amplifier.equals(NumberRange.IntRange.ANY)){
                Integer min = amplifier.getMin();
                Integer max = amplifier.getMax();
                if (Objects.equals(min, max) && min != null){
                    list.add(LText.translatable("emi_loot.entity_predicate.effect.amplifier", name.getString(), min + 1));
                } else if (min != null && max != null) {
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.amplifier_2", name.getString(), min + 1, max + 1));
                } else {
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.amplifier_3", name.getString()));
                }
            }

            NumberRange.IntRange duration = ((EffectDataAccessor)data).getDuration();
            if (!duration.equals(NumberRange.IntRange.ANY)){
                Integer min = duration.getMin();
                Integer max = duration.getMax();
                if (Objects.equals(min, max) && min != null){
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.duration", name.getString(), min + 1));
                } else if (min != null && max != null) {
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.duration_2", name.getString(), min + 1, max + 1));
                } else {
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.duration_3", name.getString()));
                }
            }

            Boolean ambient = ((EffectDataAccessor)data).getAmbient();
            if (ambient != null){
                if (ambient){
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.ambient_true", name.getString()));
                } else {
                    list.add( LText.translatable("emi_loot.entity_predicate.effect.ambient_false", name.getString()));
                }
            }

            Boolean visible = ((EffectDataAccessor)data).getVisible();
            if (visible != null){
                if (visible){
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
