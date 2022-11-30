package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.mixins.NbtPredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.text.Text;

public class NbtPredicateParser {

    public static Text parseNbtPredicate(NbtPredicate predicate){
        NbtCompound nbt = ((NbtPredicateAccessor)predicate).getNbt();
        return LText.translatable("emi_loot.entity_predicate.nbt", nbt.toString());
    }
}
