package fzzyhmstrs.emi_loot.client;

import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.ConditionalStack;
import fzzyhmstrs.emi_loot.util.TextKey;
import io.netty.handler.codec.DecoderException;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

abstract public class AbstractTextKeyParsingClientLootTable<T extends LootReceiver> implements LootReceiver {

    public AbstractTextKeyParsingClientLootTable() {
        this.rawItems = new HashMap<>();
    }

    public AbstractTextKeyParsingClientLootTable(Map<List<TextKey>, ClientRawPool> map) {
        this.rawItems = map;
    }

    private final Map<List<TextKey>, ClientRawPool> rawItems;
    public List<ClientBuiltPool> builtItems;


    static Identifier getIdFromBuf(PacketByteBuf buf) {
        String idToParse = buf.readString();
        if (idToParse.contains(":")) {
            return Identifier.of(idToParse);
        } else if (idToParse.startsWith("b/")) {
            return Identifier.of("blocks/" + idToParse.substring(2));
        } else if (idToParse.startsWith("e/")) {
            return Identifier.of("entities/" + idToParse.substring(2));
        } else if (idToParse.startsWith("c/")) {
            return Identifier.of("chests/" + idToParse.substring(2));
        } else if (idToParse.startsWith("g/")) {
            return Identifier.of("gameplay/" + idToParse.substring(2));
        } else if (idToParse.startsWith("a/")) {
            return Identifier.of("archaeology/" + idToParse.substring(2));
        } else {
            return Identifier.of(idToParse);
        }
    }

    abstract void getSpecialTextKeyList(World world, Block block, List<Pair<Integer, Text>> inputList);

    public void build(World world, Block block) {
        boolean bl = Registries.BLOCK.getEntry(block).isIn(BlockTags.SLABS);
        Map<List<Pair<Integer, Text>>, Object2FloatMap<ItemStack>> builderItems = new LinkedHashMap<>();
        rawItems.forEach((list, pool)-> {
            List<Pair<Integer, Text>> applyToAllList = new ArrayList<>();
            getSpecialTextKeyList(world, block, applyToAllList);
            for (TextKey textKey: list) {
                Text text = textKey.processText();
                applyToAllList.add(new Pair<>(textKey.index(), text));
            }
            pool.map().forEach((poolList, poolItemMap)-> {
                List<Pair<Integer, Text>> newPoolList = new ArrayList<>();
                Object2FloatMap<ItemStack> itemsToAdd = new Object2FloatOpenHashMap<>();
                List<ItemStack> itemsToRemove = new ArrayList<>();

                for (TextKey textKey : poolList) {
                    poolItemMap.forEach((poolStack, weight)-> {
                        List<ItemStack> stacks = textKey.processStack(poolStack, world);
                        float toAddWeight = 1.0f;
                        if (!stacks.contains(poolStack)) {
                            itemsToRemove.add(poolStack);
                            toAddWeight = weight;
                        }

                        for (ItemStack stack: stacks) {
                            if(poolItemMap.containsKey(stack)) {
                                toAddWeight = poolItemMap.getFloat(stack);
                            }
                        }
                        for (ItemStack stack: stacks) {
                            if(!poolItemMap.containsKey(stack)) {
                                itemsToAdd.put(stack, toAddWeight);
                            }
                        }

                    });
                    Text text = textKey.processText();
                    newPoolList.add(new Pair<>(textKey.index(), text));
                }

                for (TextKey textKey: list) {
                    poolItemMap.forEach((poolStack, weight) -> {
                        List<ItemStack> stacks = textKey.processStack(poolStack, world);
                        float toAddWeight = 1.0f;
                        if (!stacks.contains(poolStack)) {
                            itemsToRemove.add(poolStack);
                            toAddWeight = weight;
                        }

                        for (ItemStack stack: stacks) {
                            if(poolItemMap.containsKey(stack)) {
                                toAddWeight = poolItemMap.getFloat(stack);
                            }
                        }
                        for (ItemStack stack: stacks) {
                            if(!poolItemMap.containsKey(stack)) {
                                itemsToAdd.put(stack, toAddWeight);
                            }
                        }
                    });
                }
                List<Pair<Integer, Text>> summedList;
                if (applyToAllList.isEmpty()) {
                    if (newPoolList.isEmpty() && (!EMILoot.config.skippedKeys.contains("emi_loot.no_conditions") || !EMILoot.config.isTooltipStyle())) {
                        summedList = TextKey.noConditionsList.get();
                    } else {
                        summedList = newPoolList;
                    }
                } else {
                    summedList = new ArrayList<>(applyToAllList);
                    summedList.addAll(newPoolList);
                }

                Object2FloatMap<ItemStack> builderPoolMap = builderItems.getOrDefault(summedList, poolItemMap);
                builderPoolMap.putAll(itemsToAdd);
                itemsToRemove.forEach(builderPoolMap::removeFloat);
                builderItems.put(summedList, builderPoolMap);
            });

        });
        List<ClientBuiltPool> finalList = new ArrayList<>();
        builderItems.forEach((builtList, builtMap)-> {
            Float2ObjectMap<List<ItemStack>> consolidatedMap = new Float2ObjectArrayMap<>();
            builtMap.forEach((stack, weight)-> {
                List<ItemStack> consolidatedList = consolidatedMap.getOrDefault((float)weight, new ArrayList<>());
                if (!consolidatedList.contains(stack)) {
                    consolidatedList.add(stack);
                }
                consolidatedMap.put((float)weight, consolidatedList);
            });
            Float2ObjectMap<List<ItemStack>> emiConsolidatedMap = new Float2ObjectArrayMap<>();
            consolidatedMap.forEach((consolidatedWeight, consolidatedList)-> {
                emiConsolidatedMap.put((float) consolidatedWeight, consolidatedList);
            });
            finalList.add(new ClientBuiltPool(builtList, emiConsolidatedMap.float2ObjectEntrySet().stream().map(entry -> {
                List<ItemStack> sortedList = entry.getValue().stream().sorted(Comparator.comparingInt(s -> Registries.ITEM.getRawId(s.getItem()))).toList();
                return new ConditionalStack(builtList, entry.getFloatKey(), sortedList);
            }).toList()));
        });
        builtItems = finalList;
    }

    abstract Pair<Identifier, Identifier> getBufId(PacketByteBuf buf);

    abstract T simpleTableToReturn(Pair<Identifier, Identifier> ids, PacketByteBuf buf);

    abstract T emptyTableToReturn();

    abstract T filledTableToReturn(Pair<Identifier, Identifier> ids, Map<List<TextKey>, ClientRawPool> itemMap);

    @Override
    public LootReceiver fromBuf(PacketByteBuf buf) {
        boolean isEmpty = true;
        Pair<Identifier, Identifier> ids;
        Identifier id;
        try {
            ids = getBufId(buf);
            id = ids.getLeft();
        } catch (Throwable e) {
            throw new DecoderException("Parsing of Loot Receiver failed before id could be parsed", e);
        }
        try {
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("parsing table {}", id);
            int builderCount = buf.readShort();
            if (builderCount == -1) {
                return simpleTableToReturn(ids, buf);
            }

            Map<List<TextKey>, ClientRawPool> itemMap = new LinkedHashMap<>();
            //shortcut -1 means a simple table. One guaranteed drop of quantity 1 with no conditions.

            for (int b = 0; b < builderCount; b++) {

                int conditionSize = buf.readShort();

                List<TextKey> qualifierList = new ArrayList<>(conditionSize + 1);

                for (int i = 0; i < conditionSize; i++) {
                    try {
                        TextKey key = TextKey.fromBuf(buf);
                        qualifierList.add(key);
                    } catch (DecoderException e) {
						EMILoot.LOGGER.error("Client table {} had a TextKey decoding error while reading a loot condition!", id);
                    }
                }

                int functionSize = buf.readShort();
                for (int i = 0; i < functionSize; i++) {
                    try {
                        TextKey key = TextKey.fromBuf(buf);
                        qualifierList.add(key);
                    } catch (DecoderException e) {
						EMILoot.LOGGER.error("Client table {} had a TextKey decoding error while reading a loot function!", id);
                    }
                }

                ClientRawPool pool = itemMap.getOrDefault(qualifierList, new ClientRawPool(new LinkedHashMap<>()));

                int pileSize = buf.readShort();
                for (int i = 0; i < pileSize; i++) {

                    int pileQualifierSize = buf.readShort();

                    List<TextKey> pileQualifierList = new ArrayList<>(pileQualifierSize);

                    for (int j = 0; j < pileQualifierSize; j++) {
                        try {
                            TextKey key = TextKey.fromBuf(buf);
                            pileQualifierList.add(key);
                        } catch (DecoderException e) {
							EMILoot.LOGGER.error("Client table {} had a TextKey decoding error while reading an item pile qualifier!", id);
                        }
                    }

                    Object2FloatMap<ItemStack> pileItemMap = pool.map().getOrDefault(pileQualifierList, new Object2FloatOpenHashMap<>());

                    int pileItemSize = buf.readShort();
                    for (int j = 0; j < pileItemSize; j++) {
                        ItemStack stack = readItemStack(buf);
                        float weight = buf.readFloat();
                        pileItemMap.put(stack, weight);
                        isEmpty = false;
                    }
                    pool.map().put(pileQualifierList, pileItemMap);
                }

                itemMap.put(qualifierList, pool);
            }
            if (isEmpty) return emptyTableToReturn();

            return filledTableToReturn(ids, itemMap);
        } catch (Throwable e) {
            throw new DecoderException("Parsing of Loot Receiver failed for id: " + id.toString(), e);
        }
    }
}