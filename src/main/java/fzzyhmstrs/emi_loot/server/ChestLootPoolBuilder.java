package fzzyhmstrs.emi_loot.server;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ChestLootPoolBuilder implements LootBuilder {

    public ChestLootPoolBuilder(float rollWeight){
        this.rollWeight = rollWeight;
    }

    final HashMap<ItemStack, Integer> map = new HashMap<>();
    final float rollWeight;
    Integer totalWeight = 0;
    HashMap<ItemStack, Float> builtMap = new HashMap<>();


    public void addItem(ItemStack item, int weight){
        totalWeight += weight;
        if (map.containsKey(item)){
            int oldWeight = map.getOrDefault(item,0);
            map.put(item,oldWeight + weight);
        } else {
            map.put(item,weight);
        }
    }

    @Override
    public void build() {
        HashMap<ItemStack, Float> floatMap = new HashMap<>();
        map.forEach((item, itemWeight)-> {
            if (!item.isOf(Items.AIR)) {
                floatMap.put(item, (itemWeight.floatValue() / totalWeight * 100F * rollWeight));
            }
        });
        builtMap = floatMap;
    }

    @Override
    public List<LootTableParser.ItemEntryResult> revert() {
        List<LootTableParser.ItemEntryResult> list = new LinkedList<>();
        map.forEach((stack,weight)->{
            list.add(new LootTableParser.ItemEntryResult(stack,weight,new LinkedList<>(), new LinkedList<>()));
        });
        return list;
    }
}
