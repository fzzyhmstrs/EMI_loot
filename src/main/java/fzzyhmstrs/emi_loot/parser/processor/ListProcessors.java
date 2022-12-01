package fzzyhmstrs.emi_loot.parser.processor;

import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public class ListProcessors {

    public static Text buildList(List<MutableText> list, int currentIndex,String list2Key, String list3Key){
        if (currentIndex == (list.size() - 1)){
            return list.get(currentIndex);
        } else if (currentIndex == (list.size() - 2)){
            return LText.translatable(list2Key,list.get(currentIndex),buildList(list, currentIndex + 1, list2Key, list3Key).getString());
        } else {
            return LText.translatable(list3Key,list.get(currentIndex),buildList(list, currentIndex + 1, list2Key, list3Key).getString());
        }
    }

}
