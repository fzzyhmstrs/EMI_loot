package fzzyhmstrs.emi_loot.server;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class BlockLootTableSender implements LootSender<BlockLootPoolBuilder> {

    public BlockLootTableSender(Identifier id){
        this.id = id;
    }

    private final Identifier id;
    private final List<BlockLootPoolBuilder> builderList = new LinkedList<>();

    @Override
    public void send(ServerPlayerEntity player) {

    }

    @Override
    public void addBuilder(BlockLootPoolBuilder builder) {

    }
}
