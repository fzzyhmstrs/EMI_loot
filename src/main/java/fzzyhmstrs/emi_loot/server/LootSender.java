package fzzyhmstrs.emi_loot.server;

import net.minecraft.server.network.ServerPlayerEntity;

public interface LootSender {
    void send(ServerPlayerEntity player);
}
