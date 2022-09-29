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


public class EmptyLootTableSender implements LootSender<ChestLootPoolBuilder> {

    @Override
    public void send(ServerPlayerEntity player) {
    }

    @Override
    public void addBuilder(ChestLootPoolBuilder builder) {

    }
}
