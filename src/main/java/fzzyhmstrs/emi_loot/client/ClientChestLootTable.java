package fzzyhmstrs.emi_loot.client;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class ClientChestLootTable implements LootReceiver {

    public static ClientChestLootTable INSTANCE = new ClientChestLootTable();
    public final Identifier id;
    public final Map<Item, Float> items;

    public ClientChestLootTable(){
        this.id = new Identifier("empty");
        this.items = new HashMap<>();
    }

    public ClientChestLootTable(Identifier id, Map<Item,Float> map){
        this.id = id;
        this.items = map;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public LootReceiver fromBuf(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        int mapCount = buf.readShort();
        Map<Item, Float> itemMap = new HashMap<>();
        for (int i = 0; i < mapCount; i++){
            Identifier itemId = buf.readIdentifier();
            float itemWeight = buf.readFloat();
            Item item = Registry.ITEM.get(itemId);
            if (item == Items.AIR) continue;
            itemMap.put(item,itemWeight);
        }
        return new ClientChestLootTable(id,itemMap);
    }
}
