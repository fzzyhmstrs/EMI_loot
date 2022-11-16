package fzzyhmstrs.emi_loot.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;


public class LText{

    public static MutableText translatable(String key){
        return Text.translatable(key);
    }
    
    public static MutableText translatable(String key, Object ... args){
        return Text.translatable(key, args);
    }

    public static MutableText literal(String msg){
        return Text.literal(msg);
    }
    
    public static MutableText empty(){
        return Text.empty();
    }

}
