package fzzyhmstrs.emi_loot.util;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.lang.Math;
import java.util.*;
import java.util.function.Function;

public record TextKey(int index, List<String> args){

    static final Map<String, Integer> keyMap = new HashMap<>();
    static final Map<Integer,String> keyReverseMap = new HashMap<>();
    static final Map<Integer, Function<TextKey, Text>> keyTextBuilderMap = new HashMap<>();
    static final Map<Integer, Identifier> keySpriteIdMap = new HashMap<>();
    static final Function<TextKey, Text> DEFAULT_FUNCTION = (key)-> LText.translatable("emi_loot.missing_key");
    static final Identifier EMPTY = new Identifier(EMILoot.MOD_ID,"textures/gui/empty.png");
    static int curDynamicIndex = 1000;

    static{
        mapBuilder(0,"emi_loot.function.empty",(key)->LText.empty(), EMPTY);
        mapBuilder(1,"emi_loot.function.bonus",(key)-> getOneArgText(1, key), new Identifier(EMILoot.MOD_ID,"textures/gui/bonus.png"));
        mapBuilder(2,"emi_loot.function.potion",(key) -> getOneArgText(2, key), new Identifier(EMILoot.MOD_ID,"textures/gui/potion.png"));
        mapBuilder(3,"emi_loot.function.set_count_add",(key)-> getBasicText(3), new Identifier(EMILoot.MOD_ID,"textures/gui/set_count_add.png"));
        mapBuilder(4,"emi_loot.function.set_count_set",(key)-> getBasicText(4), new Identifier(EMILoot.MOD_ID,"textures/gui/set_count_add.png"));
        mapBuilder(5,"emi_loot.function.randomly_enchanted_book",(key)-> getBasicText(5), new Identifier(EMILoot.MOD_ID,"textures/gui/random_book.png"));
        mapBuilder(6,"emi_loot.function.randomly_enchanted_item",(key)-> getBasicText(6), new Identifier(EMILoot.MOD_ID,"textures/gui/random_item.png"));
        mapBuilder(7,"emi_loot.function.set_enchant_book",(key)-> getBasicText(7), new Identifier(EMILoot.MOD_ID,"textures/gui/set_book.png"));
        mapBuilder(8,"emi_loot.function.set_enchant_item",(key)-> getBasicText(8), new Identifier(EMILoot.MOD_ID,"textures/gui/set_item.png"));
        mapBuilder(9,"emi_loot.function.smelt",(key)-> getBasicText(9), new Identifier(EMILoot.MOD_ID,"textures/gui/smelt.png"));
        mapBuilder(10,"emi_loot.function.looting",(key)-> getBasicText(10), new Identifier(EMILoot.MOD_ID,"textures/gui/looting.png"));
        mapBuilder(11,"emi_loot.function.map",(key) -> getOneArgText(11, key), new Identifier(EMILoot.MOD_ID,"textures/gui/map.png"));
        mapBuilder(12,"emi_loot.function.set_contents",(key)-> getBasicText(12), new Identifier(EMILoot.MOD_ID,"textures/gui/set_contents.png"));
        mapBuilder(13,"emi_loot.function.damage",(key)-> getOneArgText(13, key), new Identifier(EMILoot.MOD_ID,"textures/gui/damage.png"));
        mapBuilder(14,"emi_loot.function.copy_state",(key)-> getBasicText(14), new Identifier(EMILoot.MOD_ID,"textures/gui/copy.png"));
        mapBuilder(15,"emi_loot.function.copy_name",(key)-> getOneArgText(15, key), new Identifier(EMILoot.MOD_ID,"textures/gui/copy.png"));
        mapBuilder(16,"emi_loot.function.copy_nbt",(key)-> getBasicText(16), new Identifier(EMILoot.MOD_ID,"textures/gui/nbt.png"));
        mapBuilder(17,"emi_loot.function.decay",(key)-> getBasicText(17), new Identifier(EMILoot.MOD_ID,"textures/gui/tnt.png"));
        mapBuilder(18,"emi_loot.function.fill_player_head",(key)-> getBasicText(18), new Identifier(EMILoot.MOD_ID,"textures/gui/fzzy.png"));
        mapBuilder(19,"emi_loot.function.limit_count",(key)-> getOneArgText(19,key), new Identifier(EMILoot.MOD_ID,"textures/gui/limit_count.png"));
        mapBuilder(20,"emi_loot.function.set_attributes",(key)-> getOneArgText(20,key), new Identifier(EMILoot.MOD_ID,"textures/gui/set_attributes.png"));
        mapBuilder(21,"emi_loot.function.banner",(key)-> getBasicText(21), new Identifier(EMILoot.MOD_ID,"textures/gui/banner.png"));
        mapBuilder(22,"emi_loot.function.lore",(key)-> getBasicText(22), new Identifier(EMILoot.MOD_ID,"textures/gui/lore.png"));
        mapBuilder(23,"emi_loot.function.set_stew",(key)-> getOneArgText(23, key), new Identifier(EMILoot.MOD_ID,"textures/gui/stew.png"));
        mapBuilder(24,"emi_loot.function.set_nbt",(key)-> getBasicText(24), new Identifier(EMILoot.MOD_ID,"textures/gui/nbt.png"));
        mapBuilder(25,"emi_loot.function.set_loot_table",(key)-> getOneArgText(25, key), new Identifier(EMILoot.MOD_ID,"textures/gui/chest.png"));
        mapBuilder(34,"emi_loot.condition.survives_explosion",(key)-> getBasicText(34), new Identifier(EMILoot.MOD_ID,"textures/gui/tnt.png"));
        mapBuilder(35,"emi_loot.condition.blockstate",(key)-> getOneArgText(35, key), new Identifier(EMILoot.MOD_ID,"textures/gui/blockstate.png"));
        mapBuilder(36,"emi_loot.condition.table_bonus",(key)-> getOneArgText(36, key), new Identifier(EMILoot.MOD_ID,"textures/gui/percent.png"));
        mapBuilder(37,"emi_loot.condition.invert",(key)-> getInvertedText(37, key), new Identifier(EMILoot.MOD_ID,"textures/gui/invert.png"));
        mapBuilder(38,"emi_loot.condition.alternates",(key)-> getOneArgText(38, key), new Identifier(EMILoot.MOD_ID,"textures/gui/or.png"));
        mapBuilder(39,"emi_loot.condition.alternates_2",(key)-> getTwoArgText(39, key), new Identifier(EMILoot.MOD_ID,"textures/gui/or.png"));
        mapBuilder(40,"emi_loot.condition.alternates_3",(key)-> getAlternates3Text(40, key), new Identifier(EMILoot.MOD_ID,"textures/gui/or.png"));
        mapBuilder(41,"emi_loot.condition.killed_player",(key)-> getBasicText(41), new Identifier(EMILoot.MOD_ID,"textures/gui/steve.png"));
        mapBuilder(42,"emi_loot.condition.chance",(key)-> getOneArgText(42, key), new Identifier(EMILoot.MOD_ID,"textures/gui/percent.png"));
        mapBuilder(43,"emi_loot.condition.chance_looting",(key)-> getTwoArgText(43, key), new Identifier(EMILoot.MOD_ID,"textures/gui/chance_looting.png"));
        mapBuilder(44,"emi_loot.condition.damage_source",(key)-> getOneArgText(44, key), new Identifier(EMILoot.MOD_ID,"textures/gui/tiny_cactus.png"));
        mapBuilder(45,"emi_loot.condition.location",(key)-> getOneArgText(45, key), new Identifier(EMILoot.MOD_ID,"textures/gui/location.png"));
        mapBuilder(46,"emi_loot.condition.entity_props",(key)-> getOneArgText(46, key), new Identifier(EMILoot.MOD_ID,"textures/gui/entity_props.png"));
        mapBuilder(47,"emi_loot.condition.match_tool",(key)-> getOneArgText(47, key), new Identifier(EMILoot.MOD_ID,"textures/gui/match_tool.png"));
        mapBuilder(48,"emi_loot.condition.entity_scores",(key)-> getBasicText(48), new Identifier(EMILoot.MOD_ID,"textures/gui/score.png"));
        mapBuilder(49,"emi_loot.condition.reference",(key)-> getOneArgText(49, key), new Identifier(EMILoot.MOD_ID,"textures/gui/reference.png"));
        mapBuilder(50,"emi_loot.condition.time_check",(key)-> getOneArgText(50, key), new Identifier(EMILoot.MOD_ID,"textures/gui/time.png"));
        mapBuilder(51,"emi_loot.condition.value_check",(key)-> getTwoArgText(51, key), new Identifier(EMILoot.MOD_ID,"textures/gui/value.png"));
        mapBuilder(52,"emi_loot.condition.raining_true",(key)-> getBasicText(52), new Identifier(EMILoot.MOD_ID,"textures/gui/raining.png"));
        mapBuilder(53,"emi_loot.condition.raining_false",(key)-> getBasicText(53), new Identifier(EMILoot.MOD_ID,"textures/gui/sunny.png"));
        mapBuilder(54,"emi_loot.condition.thundering_true",(key)-> getBasicText(54), new Identifier(EMILoot.MOD_ID,"textures/gui/thundering.png"));
        mapBuilder(55,"emi_loot.condition.thundering_false",(key)-> getBasicText(55), new Identifier(EMILoot.MOD_ID,"textures/gui/not_thundering.png"));

        //tool tag textkeys
        //pickaxe
        mapBuilder(62,"emi_loot.pickaxe.wood",(key)-> getBasicText(62), new Identifier(EMILoot.MOD_ID,"textures/gui/pickaxe_wood.png"));
        mapBuilder(63,"emi_loot.pickaxe.stone",(key)-> getBasicText(63), new Identifier(EMILoot.MOD_ID,"textures/gui/pickaxe_stone.png"));
        mapBuilder(64,"emi_loot.pickaxe.iron",(key)-> getBasicText(64), new Identifier(EMILoot.MOD_ID,"textures/gui/pickaxe_iron.png"));
        mapBuilder(65,"emi_loot.pickaxe.diamond",(key)-> getBasicText(65), new Identifier(EMILoot.MOD_ID,"textures/gui/pickaxe_diamond.png"));
        mapBuilder(66,"emi_loot.pickaxe.netherite",(key)-> getBasicText(66), new Identifier(EMILoot.MOD_ID,"textures/gui/pickaxe_netherite.png"));
        //axe
        mapBuilder(77,"emi_loot.axe.wood",(key)-> getBasicText(77), new Identifier(EMILoot.MOD_ID,"textures/gui/axe_wood.png"));
        mapBuilder(78,"emi_loot.axe.stone",(key)-> getBasicText(78), new Identifier(EMILoot.MOD_ID,"textures/gui/axe_stone.png"));
        mapBuilder(79,"emi_loot.axe.iron",(key)-> getBasicText(79), new Identifier(EMILoot.MOD_ID,"textures/gui/axe_iron.png"));
        mapBuilder(80,"emi_loot.axe.diamond",(key)-> getBasicText(80), new Identifier(EMILoot.MOD_ID,"textures/gui/axe_diamond.png"));
        mapBuilder(81,"emi_loot.axe.netherite",(key)-> getBasicText(81), new Identifier(EMILoot.MOD_ID,"textures/gui/axe_netherite.png"));
        //shovel
        mapBuilder(92,"emi_loot.shovel.wood",(key)-> getBasicText(92), new Identifier(EMILoot.MOD_ID,"textures/gui/shovel_wood.png"));
        mapBuilder(93,"emi_loot.shovel.stone",(key)-> getBasicText(93), new Identifier(EMILoot.MOD_ID,"textures/gui/shovel_stone.png"));
        mapBuilder(94,"emi_loot.shovel.iron",(key)-> getBasicText(94), new Identifier(EMILoot.MOD_ID,"textures/gui/shovel_iron.png"));
        mapBuilder(95,"emi_loot.shovel.diamond",(key)-> getBasicText(95), new Identifier(EMILoot.MOD_ID,"textures/gui/shovel_diamond.png"));
        mapBuilder(96,"emi_loot.shovel.netherite",(key)-> getBasicText(96), new Identifier(EMILoot.MOD_ID,"textures/gui/shovel_netherite.png"));
        //hoe
        mapBuilder(107,"emi_loot.hoe.wood",(key)-> getBasicText(107), new Identifier(EMILoot.MOD_ID,"textures/gui/hoe_wood.png"));
        mapBuilder(108,"emi_loot.hoe.stone",(key)-> getBasicText(108), new Identifier(EMILoot.MOD_ID,"textures/gui/hoe_stone.png"));
        mapBuilder(109,"emi_loot.hoe.iron",(key)-> getBasicText(109), new Identifier(EMILoot.MOD_ID,"textures/gui/hoe_iron.png"));
        mapBuilder(110,"emi_loot.hoe.diamond",(key)-> getBasicText(110), new Identifier(EMILoot.MOD_ID,"textures/gui/hoe_diamond.png"));
        mapBuilder(111,"emi_loot.hoe.netherite",(key)-> getBasicText(111), new Identifier(EMILoot.MOD_ID,"textures/gui/hoe_netherite.png"));
        //Direct drops and builtin/lootify conditions
        mapBuilder(124,"emi_loot.condition.sequence",(key)-> getBasicText(124), new Identifier(EMILoot.MOD_ID,"textures/gui/sequence.png"));
        mapBuilder(125,"emi_loot.condition.direct_drop",(key)-> getBasicText(125), new Identifier(EMILoot.MOD_ID,"textures/gui/direct_drops.png"));
        mapBuilder(126,"emi_loot.condition.spawns_with",(key)-> getBasicText(126), new Identifier(EMILoot.MOD_ID,"textures/gui/spawns_with.png"));
        mapBuilder(127,"emi_loot.condition.creeper",(key)-> getBasicText(127), new Identifier(EMILoot.MOD_ID,"textures/gui/creeper.png"));
        mapBuilder(128,"emi_loot.condition.wither_kill",(key)-> getBasicText(128), new Identifier(EMILoot.MOD_ID,"textures/gui/wither.png"));
        mapBuilder(129,"emi_loot.function.set_any_damage",(key)-> getBasicText(129), new Identifier(EMILoot.MOD_ID,"textures/gui/damage.png"));
        mapBuilder(130,"emi_loot.function.ominous_banner",(key)-> getBasicText(130), new Identifier(EMILoot.MOD_ID,"textures/gui/ominous.png"));
        //No conditions
        mapBuilder(150,"emi_loot.no_conditions",(key)-> getBasicText(150), EMPTY);
    }

    private static void mapBuilder(int index, String key, Function<TextKey, Text> function, Identifier spriteId){
        keyMap.put(key,index);
        keyReverseMap.put(index,key);
        keyTextBuilderMap.put(index,function);
        keySpriteIdMap.put(index, spriteId);
    }
    
    public static void register(String key, int args, Identifier sprite){
        if (keyMap.containsKey(key)) throw new IllegalArgumentException("Text key [" + key + "] already registered!");
        if (!sprite.toString().contains(".png")) throw new IllegalArgumentException("Text key [" + key + "] registered with sprite identifier [" + sprite + "] that isn't a png!)");
        int index = curDynamicIndex;
        curDynamicIndex++;
        switch (args) {
            case 0 -> mapBuilder(index, key, (tk) -> getBasicText(index), sprite);
            case 1 -> mapBuilder(index, key, (tk) -> getOneArgText(index, tk), sprite);
            case 2 -> mapBuilder(index, key, (tk) -> getTwoArgText(index, tk), sprite);
            default -> mapBuilder(index, key, (tk) -> getAlternates3Text(index, tk), sprite);
        }
    }

    private static Text getBasicText(int index){
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        return LText.translatable(translationKey);
    }

    private static Text getOneArgText(int index, TextKey key){
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        String arg;
        try{
            arg = key.args.get(0);
        } catch(Exception e) {
            e.printStackTrace();
            arg = "Missing";
        }
        return LText.translatable(translationKey, arg);
    }

    private static Text getTwoArgText(int index, TextKey key){
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        String arg1;
        String arg2;
        try{
            arg1 = key.args.get(0);
        } catch(Exception e) {
            e.printStackTrace();
            arg1 = "Missing";
        }
        try{
            arg2 = key.args.get(1);
        } catch(Exception e) {
            e.printStackTrace();
            arg2 = "Missing";
        }
        return LText.translatable(translationKey,arg1,arg2);
    }

    private static Text getAlternates3Text(int index, TextKey key){
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        MutableText finalText = LText.empty();
        List<String> args = key.args;
        int size = args.size();
        for (int i = 0;i<size;i++){
            String arg = args.get(i);
            if (i == (size - 2)){
                finalText.append(LText.translatable("emi_loot.condition.alternates_3a", arg));
            } else if (i == (size - 1)){
                finalText.append(LText.translatable("emi_loot.condition.alternates", arg));
            } else {
                finalText.append(LText.translatable(translationKey,arg));
            }
        }
        return finalText;
    }
    
    private static Text getInvertedText(int index, TextKey key){
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        String arg;
        try{
            arg = key.args.get(0);
        } catch(Exception e) {
            e.printStackTrace();
            arg = "Missing";
        }
        return LText.translatable(translationKey, arg).formatted(Formatting.RED);
    }

    public boolean isNotEmpty(){
        return index != 0;
    }
    
    public static int getIndex(String key){
        return keyMap.getOrDefault(key,-1);
    }

    public static String getKey(int index){
        return keyReverseMap.getOrDefault(index,"emi_loot.function.empty");
    }
                                                                                    
    public static Identifier getSpriteId(int index){
        return keySpriteIdMap.getOrDefault(index,EMPTY);
    }

    public static TextKey empty(){
        return new TextKey(0,new LinkedList<>());
    }

    public static TextKey of(String key, String ... args){
        if (keyMap.containsKey(key)){
            return new TextKey(keyMap.get(key), Arrays.stream(args).toList());
        } else {
            return new TextKey(0,new LinkedList<>());
        }
    }

    public static TextKey of(String key, List<String> args){
        if (keyMap.containsKey(key)){
           return new TextKey(keyMap.get(key), args);
        } else {
            EMILoot.LOGGER.error("Couldn't parse TextKey with key: " + key + "and args: " + args);
            return new TextKey(0,new LinkedList<>());
        }
    }

    public static TextKey of(String key){
        return TextKey.of(key,new LinkedList<>());
    }

    public static TextKey of (String key, String arg){
        return TextKey.of(key,Collections.singletonList(arg));
    }

    public TextKeyResult process(ItemStack stack, @Nullable World world){
        ItemStack finalStack;
        List<ItemStack> finalStacks = new LinkedList<>();
        //finalStacks.add(stack);
        if (this.index == 8 && world != null){
            Optional<SmeltingRecipe> opt = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING,new SimpleInventory(stack),world);
            if (opt.isPresent()){
                // Since AbstractCookingRecipe doesn't use the registryManager we can safely pass null here
                ItemStack tempStack = opt.get().getOutput(null);
                if (!tempStack.isEmpty()) {
                    //System.out.println(tempStack);
                    finalStack = tempStack.copy();
                    finalStacks.add(finalStack);
                }
            } else {
                finalStacks.add(stack);
            }
        } else {
            finalStacks.add(stack);
        }
        Text text = keyTextBuilderMap.getOrDefault(this.index,DEFAULT_FUNCTION).apply(this);
        return new TextKeyResult(text,finalStacks);
    }
                                                                                    
    public record TextKeyResult(Text text,List<ItemStack> stacks){}

    public static TextKey fromBuf(PacketByteBuf buf){
        int key = buf.readVarInt();
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
        buf.writeVarInt(this.index);
        if (args.isEmpty()){
            buf.writeByte(0);
        } else {
            int argSize = Math.min(127,args.size());
            buf.writeByte(args.size());
            for (int i = 0; i< argSize; i++){
                String string = args.get(i);
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
