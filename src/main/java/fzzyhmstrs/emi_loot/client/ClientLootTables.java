package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.networking.ArchaeologyBufCustomPayload;
import fzzyhmstrs.emi_loot.networking.BlockBufCustomPayload;
import fzzyhmstrs.emi_loot.networking.ChestBufCustomPayload;
import fzzyhmstrs.emi_loot.networking.ClearLootCustomPayload;
import fzzyhmstrs.emi_loot.networking.GameplayBufCustomPayload;
import fzzyhmstrs.emi_loot.networking.MobBufCustomPayload;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;

import java.util.LinkedList;
import java.util.List;

public class ClientLootTables {
    private final List<LootReceiver> loots = new LinkedList<>();

    public List<LootReceiver> getLoots() {
        return loots;
    }

    public void registerClient() {

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> loots.clear());

        ClientPlayNetworking.registerGlobalReceiver(ClearLootCustomPayload.TYPE, (payload, context) ->
                loots.clear()
        );

        ClientPlayNetworking.registerGlobalReceiver(ChestBufCustomPayload.TYPE, (payload, context)-> {
            PacketByteBuf buf = payload.buf();
            LootReceiver table = ClientChestLootTable.INSTANCE.fromBuf(buf, context.player().clientWorld);
            loots.add(table);
            if (EMILoot.config.isDebug(EMILoot.Type.CHEST)) EMILoot.LOGGER.info("received chest " + table.getId());
        });

        ClientPlayNetworking.registerGlobalReceiver(BlockBufCustomPayload.TYPE, (payload, context)-> {
            PacketByteBuf buf = payload.buf();
            LootReceiver table = ClientBlockLootTable.INSTANCE.fromBuf(buf, context.player().clientWorld);
            loots.add(table);
            if (EMILoot.config.isDebug(EMILoot.Type.BLOCK)) EMILoot.LOGGER.info("received block " + table.getId());
        });

        ClientPlayNetworking.registerGlobalReceiver(MobBufCustomPayload.TYPE, (payload, context)-> {
            PacketByteBuf buf = payload.buf();
            LootReceiver table = ClientMobLootTable.INSTANCE.fromBuf(buf, context.player().clientWorld);
            loots.add(table);
            if (EMILoot.config.isDebug(EMILoot.Type.MOB)) EMILoot.LOGGER.info("received mob " + table.getId());
        });

        ClientPlayNetworking.registerGlobalReceiver(GameplayBufCustomPayload.TYPE, (payload, context)-> {
            PacketByteBuf buf = payload.buf();
            LootReceiver table = ClientGameplayLootTable.INSTANCE.fromBuf(buf, context.player().clientWorld);
            loots.add(table);
            if (EMILoot.config.isDebug(EMILoot.Type.GAMEPLAY)) EMILoot.LOGGER.info("received gameplay loot: " + table.getId());
        });

        ClientPlayNetworking.registerGlobalReceiver(ArchaeologyBufCustomPayload.TYPE, (payload, context) -> {
            PacketByteBuf buf = payload.buf();
            LootReceiver table = ClientArchaeologyLootTable.INSTANCE.fromBuf(buf, context.player().clientWorld);
            loots.add(table);
            if (EMILoot.config.isDebug(EMILoot.Type.ARCHAEOLOGY)) EMILoot.LOGGER.info("received archaeology loot: " + table.getId());
        });
    }

}