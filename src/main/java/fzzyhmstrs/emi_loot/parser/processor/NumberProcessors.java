package fzzyhmstrs.emi_loot.parser.processor;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.*;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.predicate.NumberRange;
import net.minecraft.text.MutableText;

import java.util.Objects;

public class NumberProcessors {

    public static MutableText processBoolean(Boolean input, String keyTrue, String keyFalse, Object ... args){
        if (input != null){
            if (input){
                return LText.translatable(keyTrue, args);
            } else {
                return LText.translatable(keyFalse, args);
            }
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Boolean null for keys: " + keyTrue + " / " + keyFalse + " in table: "  + LootTableParser.currentTable);
        return LText.empty();
    }

    public static MutableText processNumberRange(NumberRange<?> range, String exact, String between, String atLeast, String atMost, String fallback, Object ... args){
        if (!range.equals(NumberRange.IntRange.ANY) && !range.equals(NumberRange.FloatRange.ANY)){
            Number min = range.getMin();
            Number max = range.getMax();
            if (Objects.equals(min, max) && min != null){
                return LText.translatable(exact, min, args);
            } else if (min != null && max != null) {
                return LText.translatable(between, min, max, args);
            }else if (min != null) {
                return LText.translatable(atLeast, min, args);
            }else if (max != null) {
                return LText.translatable(atMost, max, args);
            } else {
                if (fallback.equals("")) return LText.empty();
                return LText.translatable(fallback);
            }
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Non-specific number range for keys: " + exact + " / " + between + " in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

    public static MutableText processBoundedIntUnaryOperator(BoundedIntUnaryOperator operator){
        LootNumberProvider min = ((BoundedIntUnaryOperatorAccessor)operator).getMin();
        LootNumberProvider max = ((BoundedIntUnaryOperatorAccessor)operator).getMax();
        if (min != null && max != null){
            if (min.getType() == LootNumberProviderTypes.CONSTANT && max.getType() == LootNumberProviderTypes.CONSTANT) {
                float minVal = ((ConstantLootNumberProviderAccessor)min).getValue();
                float maxVal = ((ConstantLootNumberProviderAccessor)max).getValue();
                if (minVal == maxVal) {
                    return processLootNumberProvider(min);
                }
            }
            return LText.translatable("emi_loot.operator.between", processLootNumberProvider(min), processLootNumberProvider(max));

        } else if (min != null){
            return LText.translatable("emi_loot.operator.min", processLootNumberProvider(min));
        } else if (max != null){
            return LText.translatable("emi_loot.operator.max", processLootNumberProvider(max));
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Null or undefined bounded int unary operator in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.operator.unknown");
    }

    public static MutableText processLootNumberProvider(LootNumberProvider provider){
        LootNumberProviderType type = provider.getType();
        if(type == LootNumberProviderTypes.CONSTANT){
            return LText.translatable ("emi_loot.number_provider.constant",((ConstantLootNumberProviderAccessor)provider).getValue());
        } else if(type == LootNumberProviderTypes.BINOMIAL){
            LootNumberProvider n = ((BinomialLootNumberProviderAccessor)provider).getN();
            LootNumberProvider p = ((BinomialLootNumberProviderAccessor)provider).getP();
            float nVal = getRollAvg(n);
            float pVal = getRollAvg(p);
            MutableText nValText = processLootNumberProvider(n);
            MutableText pValText = processLootNumberProvider(p);
            float avg = nVal * pVal;
            return LText.translatable("emi_loot.number_provider.binomial",nValText,pValText,avg);
        } else if(type == LootNumberProviderTypes.UNIFORM){
            LootNumberProvider min = ((UniformLootNumberProviderAccessor)provider).getMin();
            LootNumberProvider max = ((UniformLootNumberProviderAccessor)provider).getMax();
            float minVal = getRollAvg(min);
            float maxVal = getRollAvg(max);
            MutableText minValText = processLootNumberProvider(min);
            MutableText maxValText = processLootNumberProvider(max);
            float avg = (minVal + maxVal) / 2f;
            return LText.translatable("emi_loot.number_provider.uniform",minValText,maxValText,avg);
        } else if (type == LootNumberProviderTypes.SCORE){
            //LootScoreProvider lootScoreProvider = ((ScoreLootNumberProviderAccessor)provider).getTarget();
            String lootScore = ((ScoreLootNumberProviderAccessor)provider).getScore();
            float lootScale = ((ScoreLootNumberProviderAccessor)provider).getScale();
            return LText.translatable("emi_loot.number_provider.score",lootScore,lootScale);
        } else {
            if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Non-specific or undefined number provider in table: "  + LootTableParser.currentTable);
            return LText.translatable("emi_loot.number_provider.unknown");
        }
    }

    public static float getRollAvg(LootNumberProvider provider){
        LootNumberProviderType type = provider.getType();
        if(type == LootNumberProviderTypes.CONSTANT){
            return ((ConstantLootNumberProviderAccessor)provider).getValue();
        } else if(type == LootNumberProviderTypes.BINOMIAL){
            LootNumberProvider n = ((BinomialLootNumberProviderAccessor)provider).getN();
            LootNumberProvider p = ((BinomialLootNumberProviderAccessor)provider).getP();
            float nVal = getRollAvg(n);
            float pVal = getRollAvg(p);
            return nVal * pVal;
        } else if(type == LootNumberProviderTypes.UNIFORM){
            LootNumberProvider min = ((UniformLootNumberProviderAccessor)provider).getMin();
            LootNumberProvider max = ((UniformLootNumberProviderAccessor)provider).getMax();
            float minVal = getRollAvg(min);
            float maxVal = getRollAvg(max);
            return (minVal + maxVal) / 2f;
        } else if (type == LootNumberProviderTypes.SCORE){
            return 0f;
        } else {
            if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Loot number provider with unknown type: " + provider.getType().toString());
            return 0f;
        }
    }


}
