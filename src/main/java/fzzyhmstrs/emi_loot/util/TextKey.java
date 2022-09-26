package fzzyhmstrs.emi_loot.util;

import net.minecraft.network.PacketByteBuf;

import java.util.*;

public record TextKey(int index, List<String> args){

    static Map<String, Integer> keyMap;

    static{
        keyMap = new HashMap<>();
        keyMap.put("emi_loot.function.bonus",1);
        keyMap.put("emi_loot.function.potion",2);
        keyMap.put("emi_loot.function.set_count_add",3);
        keyMap.put("emi_loot.function.randomly_enchanted_book",4);
        keyMap.put("emi_loot.function.randomly_enchanted_item",5);
        keyMap.put("emi_loot.condition.survives_explosion",6);
    }

    public boolean isEmpty(){
        return index == 0;
    }

    public static TextKey empty(){
        return new TextKey(0,new LinkedList<>());
    }

    public static TextKey of(String key, List<String> args){
        if (keyMap.containsKey(key)){
           return new TextKey(keyMap.get(key), args);
        } else {
            return new TextKey(0,new LinkedList<>());
        }
    }

    public static TextKey of(String key){
        return TextKey.of(key,new LinkedList<>());
    }

    public static TextKey of (String key, String arg){
        List<String> args = new LinkedList<>();
        args.add(arg);
        return TextKey.of(key,args);
    }

    public static TextKey fromBuf(PacketByteBuf buf){
        int key = buf.readByte();
        int size = buf.readByte();
        List<String> args = new LinkedList<>();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                args.add(buf.readString());
            }
        }
        return new TextKey(key,args);
    }

    public void toBuf(PacketByteBuf buf){
        buf.writeByte(this.index);
        if (args.isEmpty()){
            buf.writeByte(0);
        } else {
            buf.writeByte(args.size());
            for (String string: args) {
                buf.writeString(string);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextKey textKey = (TextKey) o;
        return index == textKey.index && args.equals(textKey.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, args);
    }
}
