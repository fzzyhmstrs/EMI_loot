package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
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

    public ChestLootTableSender(Identifier id){
        this.id = id;
    }

    private final Identifier id;
    final List<ChestLootPoolBuilder> builderList = new LinkedList<>();
    public static Identifier CHEST_SENDER = new Identifier(EMILoot.MOD_ID,"chest_sender");

    @Override
    public void send(ServerPlayerEntity player) {
        HashMap<ItemStack, Float> floatMap = new HashMap<>();
        builderList.forEach((builder) -> {
            builder.build();
            builder.builtMap.forEach((item,weight)->{
                if (floatMap.containsKey(item)) {
                    float oldWeight = floatMap.getOrDefault(item, 0f);
                    floatMap.put(item, oldWeight + weight);
                } else {
                    floatMap.put(item, weight);
                }
            });
        });

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(id);
        buf.writeShort(floatMap.size());
        floatMap.forEach((item, floatWeight)->{
            buf.writeItemStack(item);
            buf.writeFloat(floatWeight);
        });
        ServerPlayNetworking.send(player,CHEST_SENDER, buf);
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
