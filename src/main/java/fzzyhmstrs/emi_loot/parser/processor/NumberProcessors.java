package fzzyhmstrs.emi_loot.parser.processor;

import fzzyhmstrs.emi_loot.util.LText;
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
        return LText.empty();
    }

    public static MutableText processNumberRange(NumberRange<?> range, String exact, String between, String fallback, Object ... args){
        if (!range.equals(NumberRange.IntRange.ANY) && !range.equals(NumberRange.FloatRange.ANY)){
            Number min = range.getMin();
            Number max = range.getMax();
            if (Objects.equals(min, max) && min != null){
                return LText.translatable(exact,min, args);
            } else if (min != null && max != null) {
                return LText.translatable(between,min, max, args);
            } else {
                if (fallback.equals("")) return LText.empty();
                return LText.translatable(fallback);
            }
        }
        return LText.empty();
    }


}
