package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;


public class GameplayLootTableSender implements LootSender<GameplayLootPoolBuilder> {

    public GameplayLootTableSender(Identifier id){
        this.idToSend = LootSender.getIdToSend(id);
    }

    private final String idToSend;
    final List<GameplayLootPoolBuilder> builderList = new LinkedList<>();
    public static Identifier GAMEPLAY_SENDER = new Identifier("e_l","g_s");

    @Override
    public String getId() {
        return idToSend;
    }

    @Override
    public void send(ServerPlayerEntity player) {
        //TODO
    }

    @Override
    public void addBuilder(GameplayLootPoolBuilder builder) {
        builderList.add(builder);
    }

    @Override
    public List<GameplayLootPoolBuilder> getBuilders() {
        return builderList;
    }

    @Override
    public void build() {

    }
}
