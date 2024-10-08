package fzzyhmstrs.emi_loot.client;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ClientChestLootTable implements LootReceiver {

    public static ClientChestLootTable INSTANCE = new ClientChestLootTable();
    public final Identifier id;
    public final Object2FloatMap<ItemStack> items;

    public ClientChestLootTable() {
        this.id = Identifier.of("empty");
        this.items = new Object2FloatOpenHashMap<>();
    }

    public ClientChestLootTable(Identifier id, Object2FloatMap<ItemStack> map) {
        this.id = id;
        this.items = map;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public LootReceiver fromBuf(PacketByteBuf buf, World world) {
        Identifier id = AbstractTextKeyParsingClientLootTable.getIdFromBuf(buf);
        int mapCount = buf.readShort();
        Object2FloatMap<ItemStack> itemMap = new Object2FloatOpenHashMap<>();
        for (int i = 0; i < mapCount; i++) {
            ItemStack item = readItemStack(buf, world);
            float itemWeight = buf.readFloat();
            if (item.isOf(Items.AIR)) continue;
            itemMap.put(item, itemWeight);
        }
        return new ClientChestLootTable(id, itemMap);
    }
}