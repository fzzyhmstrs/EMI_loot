package fzzyhmstrs.emi_loot.parser.processor;

import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public class ListProcessors {
    
    public static Text buildAndList(List<MutableText> list){
        return buildAndList(list, 0);
    }

    public static Text buildAndList(List<MutableText> list, int currentIndex){
        if (currentIndex == (list.size() - 1)){
            return list.get(currentIndex);
        } else if (currentIndex == (list.size() - 2)){
            return LText.translatable("emi_loot.predicate.and_2",list.get(currentIndex),buildList(list, currentIndex + 1, "emi_loot.predicate.and_2", "emi_loot.predicate.and_3").getString());
        } else {
            return LText.translatable("emi_loot.predicate.and_3",list.get(currentIndex),buildList(list, currentIndex + 1, "emi_loot.predicate.and_2", "emi_loot.predicate.and_3").getString());
        }
    }
    
    public static Text buildOrList(List<MutableText> list){
        return buildOrList(list, 0);
    }
    
    public static Text buildOrList(List<MutableText> list, int currentIndex){
        if (currentIndex == (list.size() - 1)){
            return list.get(currentIndex);
        } else if (currentIndex == (list.size() - 2)){
            return LText.translatable("emi_loot.predicate.or_2",list.get(currentIndex),buildList(list, currentIndex + 1, "emi_loot.predicate.or_2", "emi_loot.predicate.or_3").getString());
        } else {
            return LText.translatable("emi_loot.predicate.or_3",list.get(currentIndex),buildList(list, currentIndex + 1, "emi_loot.predicate.or_2", "emi_loot.predicate.or_3").getString());
        }
    }

}
