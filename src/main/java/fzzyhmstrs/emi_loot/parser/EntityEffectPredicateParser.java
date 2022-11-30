package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.mixins.*;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.*;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.List;
import java.util.LinkedList;

public class EntityEffectPredicateParser{

    public static parseEntityEffectPredicate(EntityEffectPredicate predicate){
        Map<StatusEffect, EntityEffectPredicate.EffectData> effects = ((EntityEffectPredicateAccessor)entityEffectPredicate).getEffects();
        List<MutableText> list = new LinkedList();
        effects.forEach((effect,data) -> {
            NumberRange.IntRange amplifier = ((EffectDataAccessor)data).getAmplifier();
            NumberRange.IntRange duration = ((EffectDataAccessor)data).getDuration();
            Boolean ambient = ((EffectDataAccessor)data).getAmbient();
            Boolean visible = ((EffectDataAccessor)data).getVisible();
        
        });
        
    }

}
