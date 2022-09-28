package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ClientBlockLootTable implements LootReceiver {

    public static ClientBlockLootTable INSTANCE = new ClientBlockLootTable();
    public final Identifier id;
    public final Identifier blockId;
    private final Map<List<TextKey>, ClientBlockRawPool> rawItems;
    public Map<List<Text>, ClientBlockBuiltPool> builtItems;

    public ClientBlockLootTable(){
        this.id = new Identifier("blocks/empty");
        this.blockId = new Identifier("air");
        this.rawItems = new HashMap<>();
    }

    public ClientBlockLootTable(Identifier id, Map<List<TextKey>, ClientBlockRawPool> map){
        this.id = id;
        String ns = id.getNamespace();
        String pth = id.getPath();
        int lastSlashIndex = pth.lastIndexOf('/');
        if (lastSlashIndex == -1){
            blockId = new Identifier(ns,pth);
        } else {
            blockId = new Identifier(ns,pth.substring(Math.min(lastSlashIndex + 1,pth.length())));
        }
        this.rawItems = map;
    }

    public void build(World world){
        Map<List<Text>, ClientBlockBuiltPool> builderItems = new HashMap<>();
        rawItems.forEach((list,pool)->{
            List<Text> newList = new LinkedList<>();
            list.forEach((textKey) -> {
                Text text = textKey.process(ItemStack.EMPTY,world).text();
                newList.add(text);
            });

            ClientBlockBuiltPool newPool = builderItems.getOrDefault(newList, new ClientBlockBuiltPool(new HashMap<>()));

            Map<List<Text>,Map<ItemStack,Float>> builderPoolMap = newPool.map;
            pool.map.forEach((poolList,poolItemMap)->{

                List<Text> newPoolList = new LinkedList<>();
                Map<ItemStack, Float> itemsToAdd = new HashMap<>();

                poolList.forEach((textKey) -> {
                    poolItemMap.forEach((poolStack,weight)->{
                        List<ItemStack> stacks = textKey.process(ItemStack.EMPTY,world).stacks();
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
        Identifier id = buf.readIdentifier();
        int builderCount = buf.readByte();

        Map<List<TextKey>, ClientBlockRawPool> itemMap = new HashMap<>();

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

            ClientBlockRawPool pool = itemMap.getOrDefault(qualifierList,new ClientBlockRawPool(new HashMap<>()));

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
                }
                pool.map.put(pileQualifierList,pileItemMap);
            }

            itemMap.put(qualifierList,pool);


        }
        return new ClientBlockLootTable(id,itemMap);
    }

    public record ClientBlockRawPool(Map<List<TextKey>, Map<ItemStack,Float>> map){}
    public record ClientBlockBuiltPool(Map<List<Text>, Map<ItemStack,Float>> map){}
}
