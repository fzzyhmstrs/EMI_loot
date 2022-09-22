package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.mixins.*;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.Item;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class LootTableParser {

    private static final List<ChestLootPoolSender> chestSenders = new LinkedList<>();

    private static void onLootTableLoading(ResourceManager resourceManager, LootManager lootManager, Identifier id, FabricLootSupplierBuilder table, LootTableLoadingCallback.LootTableSetter setter) {
        LootTable lootTable = lootManager.getTable(id);
        LootContextType type = lootTable.getType();
        if (type == LootContextTypes.CHEST) {
            LootPool[] pools = ((LootTableAccessor)lootTable).getPools();
            for (LootPool pool: pools){
                LootNumberProvider rollProvider = ((LootPoolAccessor)pool).getRolls();
                float rollAvg = getRollAvg(rollProvider);
                ChestLootPoolSender sender = new ChestLootPoolSender(id, rollAvg);
                LootPoolEntry[] entries = ((LootPoolAccessor)pool).getEntries();
                for (LootPoolEntry entry: entries){
                    if (entry instanceof ItemEntry itemEntry){
                        int weight = ((LeafEntryAccessor)itemEntry).getWeight();
                        Item item = ((ItemEntryAccessor)itemEntry).getItem();
                        sender.addItem(item, weight);
                    }
                }
                chestSenders.add(sender);
            }
        }
    }

    private static float getRollAvg(LootNumberProvider provider){
        LootNumberProviderType type = provider.getType();
        if(type == LootNumberProviderTypes.CONSTANT){
            return ((ConstantLootNumberProviderAccessor)provider).getValue();
        } else if(type == LootNumberProviderTypes.BINOMIAL){
            LootNumberProvider n = ((BinomialLootNumberProviderAccessor)provider).getN();
            LootNumberProvider p = ((BinomialLootNumberProviderAccessor)provider).getP();
            float nVal = getRollAvg(n);
            float pVal = getRollAvg(p);
            return nVal * pVal;
        } else if(type == LootNumberProviderTypes.UNIFORM){
            LootNumberProvider min = ((UniformLootNumberProviderAccessor)provider).getMin();
            LootNumberProvider max = ((UniformLootNumberProviderAccessor)provider).getMax();
            float minVal = getRollAvg(min);
            float maxVal = getRollAvg(max);
            return (minVal + maxVal) / 2f;
        } else if (type == LootNumberProviderTypes.SCORE){
            return 0f;
        } else {
            return 0f;
        }
    }


    public void registerServer(){
        LootTableLoadingCallback.EVENT.register(
                LootTableParser::onLootTableLoading
        );
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> chestSenders.forEach(chestSender -> chestSender.send(handler.player)));
    }

}
