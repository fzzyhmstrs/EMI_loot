package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
    public Map<List<Text>, ClientMobBuiltPool> builtItems;

    public ClientMobLootTable(){
        this.id = EMPTY;
        this.mobId = new Identifier("air");
        this.rawItems = new HashMap<>();
    }

    public ClientMobLootTable(Identifier id, Map<List<TextKey>, ClientMobRawPool> map){
        this.id = id;
        String ns = id.getNamespace();
        String pth = id.getPath();
        int lastSlashIndex = pth.lastIndexOf('/');
        if (lastSlashIndex == -1){
            mobId = new Identifier(ns,pth);
        } else {
            String subString = pth.substring(Math.min(lastSlashIndex + 1,pth.length()));
            Identifier tempMobId = new Identifier(ns,subString);
            if (!Registry.ENTITY_TYPE.containsId(tempMobId)){
                String choppedString = pth.substring(0, lastSlashIndex);
                int nextSlashIndex = choppedString.lastIndexOf('/');
                if (nextSlashIndex != -1){
                    String sheepString = choppedString.substring(Math.min(nextSlashIndex + 1,pth.length()));
                    tempMobId = new Identifier(ns,sheepString);
                    mobId = tempMobId;
                    if (Registry.ENTITY_TYPE.containsId(tempMobId)) {
                        this.color = subString;
                    }
                } else {
                    mobId = tempMobId;
                }
            } else {
                mobId = tempMobId;
            }
        }

        this.rawItems = map;
    }

    public boolean isEmpty(){
        return Objects.equals(id, EMPTY);
    }

    public void build(World world){
        Map<List<Text>, ClientMobBuiltPool> builderItems = new HashMap<>();
        rawItems.forEach((list,pool)->{
            List<Text> newList = new LinkedList<>();
            list.forEach((textKey) -> {
                Text text = textKey.process(ItemStack.EMPTY,world).text();
                newList.add(text);
            });

            ClientMobBuiltPool newPool = builderItems.getOrDefault(newList, new ClientMobBuiltPool(new HashMap<>()));

            Map<List<Text>,Map<ItemStack,Float>> builderPoolMap = newPool.map;
            pool.map.forEach((poolList,poolItemMap)->{

                List<Text> newPoolList = new LinkedList<>();
                Map<ItemStack, Float> itemsToAdd = new HashMap<>();

                poolList.forEach((textKey) -> {
                    poolItemMap.forEach((poolStack,weight)->{
                        List<ItemStack> stacks = textKey.process(poolStack,world).stacks();
                        if (stacks.size() > 1){
                            AtomicReference<Float> toAddWeight = new AtomicReference<>(1.0f);
                            stacks.forEach(stack->{
                                if(poolItemMap.containsKey(stack)){
                                    toAddWeight.set(poolItemMap.get(stack));
                                }
                            });
                            stacks.forEach(stack->{
                                if(!poolItemMap.containsKey(stack)){
                                    itemsToAdd.put(stack,toAddWeight.get());
                                }
                            });
                        }
                    });
                    Text text = textKey.process(ItemStack.EMPTY,world).text();
                    newPoolList.add(text);

                });
                Map<ItemStack,Float> newPoolItemMap = builderPoolMap.getOrDefault(newPoolList,poolItemMap);
                newPoolItemMap.putAll(itemsToAdd);
                builderPoolMap.put(newPoolList,newPoolItemMap);

            });

            builderItems.put(newList,newPool);

        });
        builtItems = builderItems;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public LootReceiver fromBuf(PacketByteBuf buf) {
        boolean isEmpty = true;
        Identifier id = buf.readIdentifier();
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

                Map<ItemStack,Float> pileItemMap = pool.map.getOrDefault(pileQualifierList,new HashMap<>());

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

        return new ClientMobLootTable(id,itemMap);
    }

    public record ClientMobRawPool(Map<List<TextKey>, Map<ItemStack,Float>> map){}
    public record ClientMobBuiltPool(Map<List<Text>, Map<ItemStack,Float>> map){}
}
