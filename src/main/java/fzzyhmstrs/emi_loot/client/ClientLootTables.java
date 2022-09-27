package fzzyhmstrs.emi_loot.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.LinkedList;
import java.util.List;

import static fzzyhmstrs.emi_loot.server.BlockLootTableSender.BLOCK_SENDER;
import static fzzyhmstrs.emi_loot.server.ChestLootTableSender.CHEST_SENDER;

public class ClientLootTables {
    private final List<LootReceiver> loots = new LinkedList<>();

    public List<LootReceiver> getLoots(){
        return loots;
    }

    public void registerClient(){
        ClientPlayNetworking.registerGlobalReceiver(CHEST_SENDER,(minecraftClient, playNetworkHandler, buf, sender)-> {
            LootReceiver table = ClientChestLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            System.out.println("received chest " + table.getId());
        });
        ClientPlayNetworking.registerGlobalReceiver(BLOCK_SENDER,(minecraftClient, playNetworkHandler, buf, sender)-> {
            LootReceiver table = ClientBlockLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            System.out.println("received block " + table.getId());
        });
    }

}
