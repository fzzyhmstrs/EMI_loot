package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.LinkedList;
import java.util.List;

public class ChestLootPoolBuilder implements LootBuilder {

    public ChestLootPoolBuilder(float rollWeight){
        this.rollWeight = rollWeight;
    }

    final Object2IntMap<ItemStack> map = new Object2IntOpenHashMap<>();
    final float rollWeight;
    Integer totalWeight = 0;
    Object2FloatMap<ItemStack> builtMap = new Object2FloatOpenHashMap<>();


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
        Object2FloatMap<ItemStack> floatMap = new Object2FloatOpenHashMap<>();
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
        map.forEach((stack,weight)-> list.add(new LootTableParser.ItemEntryResult(stack,weight,new LinkedList<>(), new LinkedList<>())));
        return list;
    }
}
