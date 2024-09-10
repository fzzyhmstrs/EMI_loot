package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.util.SimpleCustomPayload;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class ChestLootTableSender implements LootSender<ChestLootPoolBuilder> {

    public ChestLootTableSender(Identifier id) {
        this.idToSend = LootSender.getIdToSend(id);
    }

    private final String idToSend;
    final List<ChestLootPoolBuilder> builderList = new LinkedList<>();
    HashMap<ItemStack, Float> floatMap = new HashMap<>();
    public static Identifier CHEST_SENDER = new Identifier("e_l", "c_s");

    @Override
    public void build() {
        builderList.forEach((builder) -> {
            builder.build();
            builder.builtMap.forEach((item, weight)-> {
                if (floatMap.containsKey(item)) {
                    float oldWeight = floatMap.getOrDefault(item, 0f);
                    floatMap.put(item, oldWeight + weight);
                } else {
                    floatMap.put(item, weight);
                }
            });
        });
    }

    @Override
    public String getId() {
        return idToSend;
    }

    @Override
    public void send(ServerPlayerEntity player) {
        if (!ConfigApi.INSTANCE.network().canSend(CHEST_SENDER, player)) return;
        PacketByteBuf buf = ConfigApi.INSTANCE.network().buf();
        buf.writeString(idToSend);
        buf.writeShort(floatMap.size());
        floatMap.forEach((item, floatWeight) -> {
            buf.writeItemStack(item);
            buf.writeFloat(floatWeight);
        });
        ConfigApi.INSTANCE.network().send(new SimpleCustomPayload(buf, CHEST_SENDER), player);

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