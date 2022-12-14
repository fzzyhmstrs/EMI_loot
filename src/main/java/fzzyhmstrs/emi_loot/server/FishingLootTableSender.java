package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;


public class FishingLootTableSender implements LootSender<ChestLootPoolBuilder> {

    public FishingLootTableSender(Identifier id){
        this.id = id;
    }

    private final Identifier id;
    final List<ChestLootPoolBuilder> builderList = new LinkedList<>();
    public static Identifier FISHING_SENDER = new Identifier(EMILoot.MOD_ID,"fish_sender");

    @Override
    public void send(ServerPlayerEntity player) {
        //TODO
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
