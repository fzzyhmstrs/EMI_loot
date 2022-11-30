package fzzyhmstrs.emi_loot.parser;


public class LightPredicateParser{

    public static Text parseLightPredicate(LightPredicate predicate){
        NumberRange.IntRange range = ((LightPredicateAccessor) predicate).getRange();
        if (range.equals(NumberRange.IntRange.ANY)) {
            return LText.translatable("emi_loot.location_predicate.light_any");
        }
        Integer min = range.getMin();
        Integer max = range.getMax();
        if (Objects.equals(min, max) && min != null) {
            return LText.translatable("emi_loot.location_predicate.light", min);
        } else {
            return LText.translatable("emi_loot.location_predicate.light_2", min, max);
        }
    }
}
