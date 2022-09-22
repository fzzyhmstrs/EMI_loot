package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.server.ChestLootPoolSender;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.LinkedList;
import java.util.List;

public class ClientLootTables {
    private final List<LootReceiver> loots = new LinkedList<>();

    public List<LootReceiver> getLoots(){
        return loots;
    }

    public void registerClient(){
        ClientPlayNetworking.registerGlobalReceiver(ChestLootPoolSender.CHEST_SENDER,(minecraftClient,playNetworkHandler,buf,sender)-> loots.add(ClientChestLootTable.INSTANCE.fromBuf(buf)));
    }

}
