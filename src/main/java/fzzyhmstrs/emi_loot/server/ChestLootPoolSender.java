package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class ChestLootPoolSender implements LootSender {

    public ChestLootPoolSender(Identifier id){
        this.id = id;
    }

    private final HashMap<Item, Integer> map = new HashMap<>();
    private final Identifier id;
    private Integer totalWeight = 0;
    public static Identifier CHEST_SENDER = new Identifier(EMILoot.MOD_ID,"chest_sender");

    public void addItem(Item item,int weight){
        totalWeight += weight;
        if (map.containsKey(item)){
            int oldWeight = map.getOrDefault(item,0);
            map.put(item,oldWeight + weight);
        } else {
            map.put(item,weight);
        }
    }

    @Override
    public void send(ServerPlayerEntity player) {
        HashMap<Item, Float> floatMap = new HashMap<>();
        map.forEach((item, itemWeight)-> floatMap.put(item,(itemWeight.floatValue()/totalWeight * 100F)));
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(id);
        buf.writeShort(floatMap.size());
        floatMap.forEach((item, floatWeight)->{
            buf.writeIdentifier(Registry.ITEM.getId(item));
            buf.writeFloat(floatWeight);
        });
        ServerPlayNetworking.send(player,CHEST_SENDER, buf);
    }
}
