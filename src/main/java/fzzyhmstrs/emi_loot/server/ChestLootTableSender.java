package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.networking.ChestBufCustomPayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class ChestLootTableSender implements LootSender<ChestLootPoolBuilder> {

    public ChestLootTableSender(Identifier id) {
        this.idToSend = LootSender.getIdToSend(id);
    }

    private final String idToSend;
    final List<ChestLootPoolBuilder> builderList = new LinkedList<>();
    HashMap<ItemStack, Float> floatMap = new HashMap<>();

    @Override
    public void build() {
        builderList.forEach((builder) -> {
            builder.build();
            builder.builtMap.forEach((item, weight)-> {
                if (floatMap.containsKey(item)) {
                    float oldWeight = floatMap.getOrDefault(item, 0f);
                    floatMap.put(item, oldWeight + weight);
                } else {
                    floatMap.put(item, weight);
                }
            });
        });
    }

    @Override
    public String getId() {
        return idToSend;
    }

    @Override
    public void send(ServerPlayerEntity player) {
        if (!ServerPlayNetworking.canSend(player, ChestBufCustomPayload.TYPE)) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(idToSend);
        buf.writeShort(floatMap.size());
        floatMap.forEach((item, floatWeight) -> {
            writeItemStack(buf, item, player.getServerWorld());
            buf.writeFloat(floatWeight);
        });
        ServerPlayNetworking.send(player, new ChestBufCustomPayload(buf));

    }

    @Override
    public void addBuilder(ChestLootPoolBuilder builder) {
        builderList.add(builder);
    }

    @Override
    public List<ChestLootPoolBuilder> getBuilders() {
        return builderList;
    }
}