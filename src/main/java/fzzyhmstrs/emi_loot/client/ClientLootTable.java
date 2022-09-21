package fzzyhmstrs.emi_loot.client;

import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ClientLootTable implements LootReceiver {

    public static ClientLootTable INSTANCE = new ClientLootTable();
    public final Identifier id;
    public final Map<Item, Float> items;

    public ClientLootTable(){
        this.id = new Identifier("empty");
        this.items = new HashMap<>();
    }

    public ClientLootTable(Identifier id, Map<Item,Float> map){
        this.id = id;
        this.items = map;
    }

    @Override
    public LootReceiver fromBuf(PacketByteBuf buf) {
        return null;
    }
}
