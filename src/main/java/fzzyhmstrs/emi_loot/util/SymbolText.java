package fzzyhmstrs.emi_loot.util;

import me.fzzyhmstrs.symbols_n_stuff.text.SymbolTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;


public class SymbolText {

    public static MutableText of(int key, Text text) {
        return SymbolTextContent.of(TextKey.symbolKey(key)).setStyle(text.getStyle()).append(" ").append(text);
    }
}