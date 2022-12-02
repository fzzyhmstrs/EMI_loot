package fzzyhmstrs.emi_loot.client;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("deprecation")
public class ClientBlockLootTable implements LootReceiver {

    public static ClientBlockLootTable INSTANCE = new ClientBlockLootTable();
    private static final Identifier EMPTY = new Identifier("blocks/empty");
    public final Identifier id;
    public final Identifier blockId;
    private final Map<List<TextKey>, ClientBlockRawPool> rawItems;
    public List<ClientBuiltPool> builtItems;

    public ClientBlockLootTable(){
        this.id = EMPTY;
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

    public boolean isEmpty(){
        return Objects.equals(id, EMPTY);
    }

    public void build(World world, Block block){
        String tool = "";
        if (block.getRegistryEntry().isIn(BlockTags.PICKAXE_MINEABLE)){
            tool = "pickaxe";
        } else if (block.getRegistryEntry().isIn(BlockTags.AXE_MINEABLE)){
            tool = "axe";
        } else if (block.getRegistryEntry().isIn(BlockTags.SHOVEL_MINEABLE)){
            tool = "shovel";
        } else if (block.getRegistryEntry().isIn(BlockTags.HOE_MINEABLE)){
            tool = "hoe";
        }
        List<Pair<Integer,Text>> toolNeededList = new LinkedList<>();
        if (!Objects.equals(tool,"")){
            String type;
            if (block.getRegistryEntry().isIn(BlockTags.NEEDS_STONE_TOOL)){
                type = "stone";
            } else if (block.getRegistryEntry().isIn(BlockTags.NEEDS_IRON_TOOL)){
                type = "iron";
            } else if (block.getRegistryEntry().isIn(BlockTags.NEEDS_DIAMOND_TOOL)){
                type = "diamond";
            } else{
                type = "wood";
            }
            String keyString = "emi_loot." + tool + "." + type;
            int keyIndex = TextKey.getIndex(keyString);
            if (keyIndex != -1){
                toolNeededList.add(new Pair<>(keyIndex,LText.translatable(keyString)));
            }
        }

        Map<List<Pair<Integer,Text>>, Object2FloatMap<ItemStack>> builderItems = new HashMap<>();
        rawItems.forEach((list,pool)->{
            List<Pair<Integer,Text>> applyToAllList = new LinkedList<>(toolNeededList);
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
                List< EmiStack > emiStacks = new LinkedList<>();
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
        if (isEmpty) return new ClientBlockLootTable();

        return new ClientBlockLootTable(id,itemMap);
    }

    public record ClientBlockRawPool(Map<List<TextKey>, Object2FloatMap<ItemStack>> map){}
}
