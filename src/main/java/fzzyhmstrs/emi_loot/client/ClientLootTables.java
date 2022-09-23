package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.server.ChestLootPoolBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.LinkedList;
import java.util.List;

public class ClientLootTables {
    private final List<LootReceiver> loots = new LinkedList<>();

    public List<LootReceiver> getLoots(){
        return loots;
    }

    public void registerClient(){
        ClientPlayNetworking.registerGlobalReceiver(ChestLootPoolBuilder.CHEST_SENDER,(minecraftClient, playNetworkHandler, buf, sender)-> {
            LootReceiver table = ClientChestLootTable.INSTANCE.fromBuf(buf);
            loots.add(table);
            System.out.println("received " + table.getId());
        });
    }

}
