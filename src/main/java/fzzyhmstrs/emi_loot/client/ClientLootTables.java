package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.EMILoot;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.LinkedList;
import java.util.List;

import static fzzyhmstrs.emi_loot.server.ArchaeologyLootTableSender.ARCHAEOLOGY_SENDER;
import static fzzyhmstrs.emi_loot.server.BlockLootTableSender.BLOCK_SENDER;
import static fzzyhmstrs.emi_loot.server.ChestLootTableSender.CHEST_SENDER;
import static fzzyhmstrs.emi_loot.server.GameplayLootTableSender.GAMEPLAY_SENDER;
import static fzzyhmstrs.emi_loot.server.MobLootTableSender.MOB_SENDER;

public class ClientLootTables {
    private final List<LootReceiver> loots = new LinkedList<>();

    public List<LootReceiver> getLoots(){
        return loots;
    }

    public void registerClient(){
        
        ClientPlayConnectionEvents.DISCONNECT.register((handler,client) -> loots.clear());
        
        ClientPlayNetworking.registerGlobalReceiver(CHEST_SENDER,(minecraftClient, playNetworkHandler, buf, sender)-> {
            LootReceiver table = ClientChestLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received chest " + table.getId());
        });
        
        ClientPlayNetworking.registerGlobalReceiver(BLOCK_SENDER,(minecraftClient, playNetworkHandler, buf, sender)-> {
            LootReceiver table = ClientBlockLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received block " + table.getId());
        });
        
        ClientPlayNetworking.registerGlobalReceiver(MOB_SENDER,(minecraftClient, playNetworkHandler, buf, sender)-> {
            LootReceiver table = ClientMobLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received mob " + table.getId());
        });
        
        ClientPlayNetworking.registerGlobalReceiver(GAMEPLAY_SENDER,(minecraftClient, playNetworkHandler, buf, sender)-> {
            LootReceiver table = ClientGameplayLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received gameplay loot: " + table.getId());
        });

        ClientPlayNetworking.registerGlobalReceiver(ARCHAEOLOGY_SENDER, (minecraftClient, playNetworkHandler, buf, sender) -> {
            LootReceiver table = ClientArchaeologyLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("received archaeology loot: " + table.getId());
        });
    }

}
