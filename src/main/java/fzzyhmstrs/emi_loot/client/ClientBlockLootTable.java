package fzzyhmstrs.emi_loot.client;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientBlockLootTable implements LootReceiver {

    public static ClientBlockLootTable INSTANCE = new ClientBlockLootTable();
    public final Identifier id;
    public final Map<Text, List<ItemStack>> items;

    public ClientBlockLootTable(){
        this.id = new Identifier("empty");
        this.items = new HashMap<>();
    }

    public ClientBlockLootTable(Identifier id, Map<Text, List<ItemStack>> map){
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
        Map<Text, List<ItemStack>> itemMap = new HashMap<>();
        /*for (int i = 0; i < mapCount; i++){
            ItemStack item = buf.readItemStack();
            float itemWeight = buf.readFloat();
            if (item.isOf(Items.AIR)) continue;
            itemMap.put(item,itemWeight);
        }*/
        return new ClientBlockLootTable(id,itemMap);
    }
}
