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


public class FishingLootTableSender implements LootSender<ChestLootPoolBuilder> {

    public FishingLootTableSender(Identifier id){
        this.id = id;
    }

    private final Identifier id;
    private final List<ChestLootPoolBuilder> builderList = new LinkedList<>();
    public static Identifier FISHING_SENDER = new Identifier(EMILoot.MOD_ID,"fish_sender");

    @Override
    public void send(ServerPlayerEntity player) {
        //TODO
    }

    @Override
    public void addBuilder(ChestLootPoolBuilder builder) {
        builderList.add(builder);
    }
}
