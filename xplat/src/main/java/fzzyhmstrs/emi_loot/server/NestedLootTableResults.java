package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.SimpleFzzyPayload;
import fzzyhmstrs.emi_loot.util.TextKey;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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