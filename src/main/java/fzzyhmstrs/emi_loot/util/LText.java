package fzzyhmstrs.emi_loot.util;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;


public class LText{

    public static MutableText translatable(String key){
        return new TranslatableText(key);
    }
    
    public static MutableText translatable(String key, Object ... args){
        return new TranslatableText(key, args);
    }

    public static MutableText literal(String msg){
        return new LiteralText(msg);
    }
    
    public static MutableText empty(){
        return (MutableText) LiteralText.EMPTY;
    }

}
