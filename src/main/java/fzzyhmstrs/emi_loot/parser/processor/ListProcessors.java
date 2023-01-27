package fzzyhmstrs.emi_loot.parser.processor;

import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.text.MutableText;

import java.util.List;

public class ListProcessors {
    
    public static MutableText buildAndList(List<MutableText> list){
        return buildAndList(list, 0);
    }

    public static MutableText buildAndList(List<MutableText> list, int currentIndex){
        if (list.size() == 0) return LText.empty();
        if (currentIndex == (list.size() - 1)){
            return list.get(currentIndex);
        } else if (currentIndex == (list.size() - 2)){
            return LText.translatable("emi_loot.predicate.and_2",list.get(currentIndex),buildAndList(list, currentIndex + 1).getString());
        } else {
            return LText.translatable("emi_loot.predicate.and_3",list.get(currentIndex),buildAndList(list, currentIndex + 1).getString());
        }
    }
    
    public static MutableText buildOrList(List<MutableText> list){
        return buildOrList(list, 0);
    }
    
    public static MutableText buildOrList(List<MutableText> list, int currentIndex){
        if (list.size() == 0) return LText.empty();
        if (currentIndex == (list.size() - 1)){
            return list.get(currentIndex);
        } else if (currentIndex == (list.size() - 2)){
            return LText.translatable("emi_loot.predicate.or_2",list.get(currentIndex),buildOrList(list, currentIndex + 1).getString());
        } else {
            return LText.translatable("emi_loot.predicate.or_3",list.get(currentIndex),buildOrList(list, currentIndex + 1).getString());
        }
    }

}
