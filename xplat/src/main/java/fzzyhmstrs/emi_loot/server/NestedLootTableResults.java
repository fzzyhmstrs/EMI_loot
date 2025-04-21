package fzzyhmstrs.emi_loot.server;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.LinkedList;
import java.util.List;

public class NestedLootTableResults implements LootSender<ComplexLootPoolBuilder> {

    public NestedLootTableResults() {}

    final List<ComplexLootPoolBuilder> builderList = new LinkedList<>();


    @Override
    public void build() {
        builderList.forEach(ComplexLootPoolBuilder::build);
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void send(ServerPlayerEntity player) {
    }

    @Override
    public void addBuilder(ComplexLootPoolBuilder builder) {
        builderList.add(builder);
    }

    @Override
    public List<ComplexLootPoolBuilder> getBuilders() {
        return builderList;
    }
}