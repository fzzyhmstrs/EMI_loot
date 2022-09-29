package fzzyhmstrs.emi_loot.server;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public interface LootSender<T extends LootBuilder> {
    void send(ServerPlayerEntity player);
    void addBuilder(T builder);
    List<T> getBuilders();
}
