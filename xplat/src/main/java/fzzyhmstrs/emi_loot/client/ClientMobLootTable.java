package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.util.TextKey;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClientMobLootTable extends AbstractTextKeyParsingClientLootTable<ClientMobLootTable> {

    public static ClientMobLootTable INSTANCE = new ClientMobLootTable();
    private static final Identifier EMPTY = Identifier.of("entity/empty");
    public final Identifier id;
    public final Identifier mobId;
    public String color = "";

    public ClientMobLootTable() {
        super();
        this.id = EMPTY;
        this.mobId = Identifier.of("empty");
    }

    public ClientMobLootTable(Identifier id, Identifier mobId, Map<List<TextKey>, ClientRawPool> map) {
        super(map);
        this.id = id;
        String ns = id.getNamespace();
        String pth = id.getPath();
        if (!Registries.ENTITY_TYPE.containsId(mobId)) {
            this.mobId = Identifier.of("empty");
        } else {
            if (Objects.equals(mobId, Registries.ENTITY_TYPE.getId(EntityType.SHEEP))) {
                int lastSlashIndex = pth.lastIndexOf('/');
                if (lastSlashIndex != -1) {
                    this.color = pth.substring(Math.min(lastSlashIndex + 1, pth.length()));
                }
            }
            this.mobId = mobId;
        }
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public boolean isEmpty() {
        return Objects.equals(id, EMPTY);
    }

    @Override
    List<Pair<Integer, Text>> getSpecialTextKeyList(World world, Block block) {
        return List.of();
    }

    @Override
    Pair<Identifier, Identifier> getBufId(PacketByteBuf buf) {
        Identifier id = getIdFromBuf(buf);
        Identifier mobId = getIdFromBuf(buf);
        return new Pair<>(id, mobId);
    }

    @Override
    ClientMobLootTable simpleTableToReturn(Pair<Identifier, Identifier> ids, PacketByteBuf buf) {
        ClientRawPool simplePool = new ClientRawPool(new HashMap<>());
        Object2FloatMap<ItemStack> simpleMap = new Object2FloatOpenHashMap<>();
        ItemStack simpleStack = new ItemStack(Registries.ITEM.getEntry(buf.readRegistryKey(RegistryKeys.ITEM)).map(RegistryEntry.Reference::value).orElse(Items.AIR));
        simpleMap.put(simpleStack, 100F);
        simplePool.map().put(new ArrayList<>(), simpleMap);
        Map<List<TextKey>, ClientRawPool> itemMap = new HashMap<>();
        itemMap.put(new ArrayList<>(), simplePool);
        return new ClientMobLootTable(ids.getLeft(), ids.getRight(), itemMap);
    }

    @Override
    ClientMobLootTable emptyTableToReturn() {
        return new ClientMobLootTable();
    }

    @Override
    ClientMobLootTable filledTableToReturn(Pair<Identifier, Identifier> ids, Map<List<TextKey>, ClientRawPool> itemMap) {
        return new ClientMobLootTable(ids.getLeft(), ids.getRight(), itemMap);
    }
}