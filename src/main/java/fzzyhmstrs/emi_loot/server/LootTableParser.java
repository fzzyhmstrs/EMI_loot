package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.mixins.ItemEntryAccessor;
import fzzyhmstrs.emi_loot.mixins.LeafEntryAccessor;
import fzzyhmstrs.emi_loot.mixins.LootPoolAccessor;
import fzzyhmstrs.emi_loot.mixins.LootTableAccessor;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LootTableParser {

    private static List<ChestLootPoolSender> chestSenders;

    private static void onLootTableLoading(ResourceManager resourceManager, LootManager lootManager, Identifier id, FabricLootSupplierBuilder table, LootTableLoadingCallback.LootTableSetter setter) {
        LootTable lootTable = lootManager.getTable(id);
        LootContextType type = lootTable.getType();
        if (type == LootContextTypes.CHEST) {
            LootPool[] pools = ((LootTableAccessor)lootTable).getPools();
            for (LootPool pool: pools){
                ChestLootPoolSender sender = new ChestLootPoolSender(id);
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


    public void registerServer(){
        LootTableLoadingCallback.EVENT.register(
                LootTableParser::onLootTableLoading
        );
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> chestSenders.forEach(chestSender -> chestSender.send(handler.player)));
    }

}
