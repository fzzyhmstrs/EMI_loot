package fzzyhmstrs.emi_loot.server;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.LinkedList;
import java.util.List;


public class EmptyLootTableSender implements LootSender<SimpleLootPoolBuilder> {

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void send(ServerPlayerEntity player) {
    }

    @Override
    public void addBuilder(SimpleLootPoolBuilder builder) {

    }

    @Override
    public List<SimpleLootPoolBuilder> getBuilders() {
        return new LinkedList<>();
    }

    @Override
    public void build() {

    }
}