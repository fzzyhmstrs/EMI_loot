package fzzyhmstrs.emi_loot.client;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ClientMobLootTable implements LootReceiver {

    public static ClientMobLootTable INSTANCE = new ClientMobLootTable();
    private static final Identifier EMPTY = new Identifier("entity/empty");
    public final Identifier id;
    public final Identifier mobId;
    public String color = "";
    private final Map<List<TextKey>, ClientMobRawPool> rawItems;
    public List<ClientBuiltPool> builtItems;

    public ClientMobLootTable(){
        this.id = EMPTY;
        this.mobId = new Identifier("empty");
        this.rawItems = new HashMap<>();
    }

    public ClientMobLootTable(Identifier id,Identifier mobId, Map<List<TextKey>, ClientMobRawPool> map){
        this.id = id;
        String ns = id.getNamespace();
        String pth = id.getPath();
        if (Objects.equals(mobId, new Identifier("empty"))) {
            int lastSlashIndex = pth.lastIndexOf('/');
            if (lastSlashIndex == -1) {
                this.mobId = new Identifier(ns, pth);
            } else {
                String subString = pth.substring(Math.min(lastSlashIndex + 1, pth.length()));
                Identifier tempMobId = new Identifier(ns, subString);
                if (!Registry.ENTITY_TYPE.containsId(tempMobId)) {
                    String choppedString = pth.substring(0, lastSlashIndex);
                    int nextSlashIndex = choppedString.lastIndexOf('/');
                    if (nextSlashIndex != -1) {
                        String sheepString = choppedString.substring(Math.min(nextSlashIndex + 1, pth.length()));
                        tempMobId = new Identifier(ns, sheepString);
                        this.mobId = tempMobId;
                        if (Registry.ENTITY_TYPE.containsId(tempMobId)) {
                            this.color = subString;
                        }
                    } else {
                        this.mobId = tempMobId;
                    }
                } else {
                    this.mobId = tempMobId;
                }
            }
        } else {
            this.mobId = mobId;
        }

        this.rawItems = map;
    }

    public boolean isEmpty(){
        return Objects.equals(id, EMPTY);
    }

    public void build(World world){
        Map<List<Pair<Integer,Text>>, Object2FloatMap<ItemStack>> builderItems = new HashMap<>();
        rawItems.forEach((list,pool)->{
            List<Pair<Integer,Text>> applyToAllList = new LinkedList<>();
            list.forEach((textKey) -> {
                Text text = textKey.process(ItemStack.EMPTY,world).text();
                applyToAllList.add(new Pair<>(textKey.index(),text));
            });
            pool.map.forEach((poolList,poolItemMap)->{
                List<Pair<Integer,Text>> newPoolList = new LinkedList<>();
                Object2FloatMap<ItemStack> itemsToAdd = new Object2FloatOpenHashMap<>();
                List<ItemStack> itemsToRemove = new LinkedList<>();

                poolList.forEach((textKey) -> {
                    poolItemMap.forEach((poolStack,weight)->{
                        List<ItemStack> stacks = textKey.process(poolStack,world).stacks();
                        AtomicReference<Float> toAddWeight = new AtomicReference<>(1.0f);
                        if (!stacks.contains(poolStack)){
                            itemsToRemove.add(poolStack);
                            toAddWeight.set(poolItemMap.getFloat(poolStack));
                        }
                        stacks.forEach(stack->{
                            if(poolItemMap.containsKey(stack)){
                                toAddWeight.set(poolItemMap.getFloat(stack));
                            }
                        });
                        stacks.forEach(stack->{
                            if(!poolItemMap.containsKey(stack)){
                                itemsToAdd.put(stack,(float)toAddWeight.get());
                            }
                        });

                    });
                    Text text = textKey.process(ItemStack.EMPTY,world).text();
                    newPoolList.add(new Pair<>(textKey.index(),text));

                });
                List<Pair<Integer, Text>> summedList = new LinkedList<>(applyToAllList);
                summedList.addAll(newPoolList);
                if (summedList.isEmpty()){
                    summedList.add(new Pair<>(63, LText.translatable("emi_loot.no_conditions")));
                }
                Object2FloatMap<ItemStack> builderPoolMap = builderItems.getOrDefault(summedList, poolItemMap);
                builderPoolMap.putAll(itemsToAdd);
                itemsToRemove.forEach(builderPoolMap::removeFloat);
                builderItems.put(summedList,builderPoolMap);

            });
        });
        List<ClientBuiltPool> finalList = new LinkedList<>();
        builderItems.forEach((builtList,builtMap)->{
            Float2ObjectMap<List<ItemStack>> consolidatedMap = new Float2ObjectArrayMap<>();
            builtMap.forEach((stack,weight)->{
                List<ItemStack> consolidatedList = consolidatedMap.getOrDefault((float)weight,new LinkedList<>());
                if (!consolidatedList.contains(stack)){
                    consolidatedList.add(stack);
                }
                consolidatedMap.put((float)weight,consolidatedList);
            });
            Float2ObjectMap<EmiIngredient> emiConsolidatedMap = new Float2ObjectArrayMap<>();
            consolidatedMap.forEach((consolidatedWeight,consolidatedList)-> {
                List<EmiStack> emiStacks = new LinkedList<>();
                for (ItemStack i : consolidatedList){
                    emiStacks.add(EmiStack.of(i));
                }
                emiConsolidatedMap.put((float) consolidatedWeight, EmiIngredient.of(emiStacks));
            });
            finalList.add(new ClientBuiltPool(builtList,emiConsolidatedMap));
        });
        builtItems = finalList;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public LootReceiver fromBuf(PacketByteBuf buf) {
        boolean isEmpty = true;
        Identifier id = buf.readIdentifier();
        Identifier mobId = buf.readIdentifier();
        int builderCount = buf.readByte();

        Map<List<TextKey>, ClientMobRawPool> itemMap = new HashMap<>();

        for (int b = 0; b < builderCount; b++) {

            List<TextKey> qualifierList = new LinkedList<>();

            int conditionSize = buf.readByte();
            for (int i = 0; i < conditionSize; i++) {
                TextKey key = TextKey.fromBuf(buf);
                qualifierList.add(key);
            }

            int functionSize = buf.readByte();
            for (int i = 0; i < functionSize; i++) {
                TextKey key = TextKey.fromBuf(buf);
                qualifierList.add(key);
            }

            ClientMobRawPool pool = itemMap.getOrDefault(qualifierList,new ClientMobRawPool(new HashMap<>()));

            int pileSize = buf.readByte();
            for (int i = 0; i < pileSize; i++) {

                List<TextKey> pileQualifierList = new LinkedList<>();

                int pileQualifierSize = buf.readByte();
                for (int j = 0; j < pileQualifierSize; j++) {
                    TextKey key = TextKey.fromBuf(buf);
                    pileQualifierList.add(key);
                }

                Object2FloatMap<ItemStack> pileItemMap = pool.map.getOrDefault(pileQualifierList,new Object2FloatOpenHashMap<>());

                int pileItemSize = buf.readByte();
                for (int j = 0; j < pileItemSize; j++) {
                    ItemStack stack = buf.readItemStack();
                    float weight = buf.readFloat();
                    pileItemMap.put(stack,weight);
                    isEmpty = false;
                }
                pool.map.put(pileQualifierList,pileItemMap);
            }

            itemMap.put(qualifierList,pool);
        }
        if (isEmpty) return new ClientMobLootTable();

        return new ClientMobLootTable(id,mobId,itemMap);
    }

    public record ClientMobRawPool(Map<List<TextKey>, Object2FloatMap<ItemStack>> map){}
}
