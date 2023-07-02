package fzzyhmstrs.emi_loot.parser;

import com.google.common.collect.Multimap;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.*;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.parser.registry.LootParserRegistry;
import fzzyhmstrs.emi_loot.server.*;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.*;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LootTableParser {

    private static final Map<Identifier, ChestLootTableSender> chestSenders = new HashMap<>();
    private static final Map<Identifier, BlockLootTableSender> blockSenders = new HashMap<>();
    private static final Map<Identifier, MobLootTableSender> mobSenders = new HashMap<>();
    private static final Map<Identifier, GameplayLootTableSender> gameplaySenders = new HashMap<>();
    private static final Map<Identifier, ArchaeologyLootTableSender> archaeologySenders = new HashMap<>();
    public static final Object2BooleanMap<PostProcessor> postProcessors;
    private static Map<LootDataKey<?>, ?> tables = new HashMap<>();
    private static final Map<Identifier,LootDataKey<?>> keyLookUp = new HashMap<>();
    public static String currentTable = "none";
    public static List<Identifier> parsedDirectDrops = new LinkedList<>();
    public static boolean hasParsedLootTables = false;
    public static LootManager lootManager = null;


    static {
        Object2BooleanOpenHashMap<PostProcessor> map = new Object2BooleanOpenHashMap<>();
        for (var value : PostProcessor.values()){
            map.put(value,false);
        }
        postProcessors = map;
    }

    private static boolean hasPostProcessed(){
        for (boolean bl: postProcessors.values()){
            if (!bl) return false;
        }
        return true;
    }

    public void registerServer(){
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->{
            if (!hasPostProcessed()){
                EMILoot.LOGGER.warn("Post-processing not completed for some reason, completing now...");
                for (PostProcessor process: PostProcessor.values()){
                    postProcess(process);
                }
                EMILoot.LOGGER.warn("Post-processing complete!");
            }
            if (EMILoot.config.parseChestLoot)
                chestSenders.forEach((id,chestSender) -> chestSender.send(handler.player));
            if (EMILoot.config.parseBlockLoot)
                blockSenders.forEach((id,blockSender) -> blockSender.send(handler.player));
            if (EMILoot.config.parseMobLoot)
                mobSenders.forEach((id,mobSender) -> mobSender.send(handler.player));
            if (EMILoot.config.parseGameplayLoot)
                gameplaySenders.forEach((id,gameplaySender) -> gameplaySender.send(handler.player));
            if (EMILoot.config.parseArchaeologyLoot)
                archaeologySenders.forEach((id, archaeologySender) -> archaeologySender.send(handler.player));
        });
    }

    public static void parseLootTables(LootManager manager, Map<LootDataKey<?>, ?> tables) {
        keyLookUp.clear();
        LootTableParser.tables = tables;
        LootTableParser.lootManager = manager;
        for (LootDataKey<?> key : LootTableParser.tables.keySet()){
            if (key.type() == LootDataType.LOOT_TABLES)
                keyLookUp.put(key.id(),key);
        }
        parsedDirectDrops = new LinkedList<>();
        EMILoot.LOGGER.info("parsing loot tables");
        tables.forEach((key, table) -> {
            if (table instanceof LootTable)
                parseLootTable(key.id(), (LootTable) table);
        });
        if (EMILoot.config.parseMobLoot) {
            Identifier chk = new Identifier("pig");
            Registries.ENTITY_TYPE.stream().toList().forEach((type) -> {
                if (type == EntityType.SHEEP){
                    for (Identifier sheepId : ServerResourceData.SHEEP_TABLES){
                        parseEntityType(manager,type,sheepId,chk);
                    }
                }
                parseEntityType(manager,type,type.getLootTableId(),chk);
            });
        }
        Multimap<Identifier, LootTable> missedDrops = ServerResourceData.getMissedDirectDrops(parsedDirectDrops);
        for (Map.Entry<Identifier,LootTable> entry : missedDrops.entries()){
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("parsing missed direct drop table: " + entry.getKey());
            parseLootTable(entry.getKey(),entry.getValue());
        }
        EMILoot.LOGGER.info("finished parsing loot tables");
        hasParsedLootTables = true;
    }

    private static void parseLootTable(Identifier id, LootTable lootTable){
        if (ServerResourceData.skipTable(id)) return;
        currentTable = id.toString();
        LootContextType type = lootTable.getType();
        if (type == LootContextTypes.CHEST && EMILoot.config.parseChestLoot) {
            chestSenders.put(id, parseChestLootTable(lootTable,id));
        } else if (type == LootContextTypes.BLOCK && EMILoot.config.parseBlockLoot) {
            blockSenders.put(id, parseBlockLootTable(lootTable,id));
        } else if ((type == LootContextTypes.FISHING || type == LootContextTypes.GIFT ||type == LootContextTypes.BARTER) && EMILoot.config.parseGameplayLoot){
            gameplaySenders.put(id, parseGameplayLootTable(lootTable, id));
        } else if ((type == LootContextTypes.ARCHAEOLOGY && EMILoot.config.parseArchaeologyLoot)) {
            archaeologySenders.put(id, parseArchaeologyTable(lootTable, id));
        }
    }


    public static void postProcess(PostProcessor process){
        if (!hasParsedLootTables) return;
        for (LootSender<?> sender : chestSenders.values()){
            for (LootBuilder builder : sender.getBuilders()){
                for (LootPoolEntry entry: builder.getEntriesToPostProcess(process)){
                    if (EMILoot.DEBUG) EMILoot.LOGGER.info("Post-processing builder in chest sender: " + sender.getId());
                    parseLootPoolEntry(builder,entry,process);
                }
            }
            sender.build();
        }
        for (LootSender<?> sender : blockSenders.values()){
            for (LootBuilder builder : sender.getBuilders()){
                for (LootPoolEntry entry: builder.getEntriesToPostProcess(process)){
                    if (EMILoot.DEBUG) EMILoot.LOGGER.info("Post-processing builder in block sender: " + sender.getId());
                    parseLootPoolEntry(builder,entry,process);
                }
            }
            sender.build();
        }
        for (LootSender<?> sender : mobSenders.values()){
            for (LootBuilder builder : sender.getBuilders()){
                for (LootPoolEntry entry: builder.getEntriesToPostProcess(process)){
                    if (EMILoot.DEBUG) EMILoot.LOGGER.info("Post-processing builder in mob sender: " + sender.getId());
                    parseLootPoolEntry(builder,entry,process);
                }
            }
            sender.build();
        }
        for (LootSender<?> sender : gameplaySenders.values()){
            for (LootBuilder builder : sender.getBuilders()){
                for (LootPoolEntry entry: builder.getEntriesToPostProcess(process)){
                    if (EMILoot.DEBUG) EMILoot.LOGGER.info("Post-processing builder in gameplay sender: " + sender.getId());
                    parseLootPoolEntry(builder,entry,process);
                }
            }
            sender.build();
        }
        for (LootSender<?> sender : archaeologySenders.values()) {
            for (LootBuilder builder : sender.getBuilders()) {
                for (LootPoolEntry entry : builder.getEntriesToPostProcess(process)) {
                    if(EMILoot.DEBUG) EMILoot.LOGGER.info("Post-processing builder in archaeology sender: " + sender.getId());
                    parseLootPoolEntry(builder, entry, process);
                }
            }
            sender.build();
        }
        postProcessors.put(process,true);
    }

    private static void parseEntityType(LootManager manager,EntityType<?> type, Identifier mobTableId, Identifier fallback){
        Identifier mobId = Registries.ENTITY_TYPE.getId(type);
        LootTable mobTable = manager.getLootTable(mobTableId);
        if (type == EntityType.PIG && mobId.equals(fallback) || mobTable != LootTable.EMPTY) {
            currentTable = mobTableId.toString();
            mobSenders.put(mobTableId, parseMobLootTable(mobTable, mobTableId, mobId));
        } else {
            if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Found empty mob table at id: " + mobTableId);
        }
    }

    private static ChestLootTableSender parseChestLootTable(LootTable lootTable, Identifier id){
        ChestLootTableSender sender = new ChestLootTableSender(id);
        for (LootPool pool : lootTable.pools) {
            LootNumberProvider rollProvider = pool.rolls;
            float rollAvg = NumberProcessors.getRollAvg(rollProvider);
            ChestLootPoolBuilder builder = new ChestLootPoolBuilder(rollAvg);
            LootPoolEntry[] entries = pool.entries;
            for (LootPoolEntry entry : entries) {
                    parseLootPoolEntry(builder,entry);
            }
            sender.addBuilder(builder);
        }
        return sender;
    }

    private static BlockLootTableSender parseBlockLootTable(LootTable lootTable, Identifier id){
        BlockLootTableSender sender = new BlockLootTableSender(id);
        parseBlockLootTableInternal(lootTable,sender, false);
        if (ServerResourceData.DIRECT_DROPS.containsKey(id) && EMILoot.config.mobLootIncludeDirectDrops){
            parsedDirectDrops.add(id);
            Collection<LootTable> directTables = ServerResourceData.DIRECT_DROPS.get(id);
            parseBlockDirectLootTable(directTables,sender);
        }
        return sender;
    }

    private static void parseBlockDirectLootTable(Collection<LootTable> tables, BlockLootTableSender sender){
        for (LootTable directTable : tables) {
            if (directTable != null) {
                parseBlockLootTableInternal(directTable, sender, true);
            }
        }
    }

    private static void parseBlockLootTableInternal(LootTable lootTable, BlockLootTableSender sender, boolean isDirect){
        for (LootPool pool : lootTable.pools) {
            LootCondition[] conditions = pool.conditions;
            List<LootConditionResult> parsedConditions = parseLootConditions(conditions,ItemStack.EMPTY,false);
            if (isDirect){
                if (EMILoot.DEBUG) EMILoot.LOGGER.info("Adding direct drop condition to " + currentTable);
                parsedConditions.add(new LootConditionResult(TextKey.of("emi_loot.condition.direct_drop")));
            }
            LootFunction[] functions = pool.functions;
            List<LootFunctionResult> parsedFunctions = new LinkedList<>();
            for (LootFunction function: functions){
                parsedFunctions.add(parseLootFunction(function));
            }
            LootNumberProvider rollProvider = pool.rolls;
            float rollAvg = NumberProcessors.getRollAvg(rollProvider);
            BlockLootPoolBuilder builder = new BlockLootPoolBuilder(rollAvg, parsedConditions, parsedFunctions);
            LootPoolEntry[] entries = pool.entries;
            for (LootPoolEntry entry : entries) {
                parseLootPoolEntry(builder,entry);
            }
            sender.addBuilder(builder);
        }
    }

    private static MobLootTableSender parseMobLootTable(LootTable lootTable, Identifier id, Identifier mobId){
        MobLootTableSender sender = new MobLootTableSender(id, mobId);
        parseMobLootTableInternal(lootTable,sender, false);
        if (ServerResourceData.DIRECT_DROPS.containsKey(id) && EMILoot.config.mobLootIncludeDirectDrops){
            parsedDirectDrops.add(id);
            Collection<LootTable> directTables = ServerResourceData.DIRECT_DROPS.get(id);
            parseMobDirectLootTable(directTables,sender);
        }
        return sender;
    }

    private static void parseMobDirectLootTable(Collection<LootTable> tables, MobLootTableSender sender){
        for (LootTable directTable : tables) {
            if (directTable != null) {
                parseMobLootTableInternal(directTable, sender, true);
            }
        }
    }

    private static void parseMobLootTableInternal(LootTable lootTable, MobLootTableSender sender, boolean isDirect){
        for (LootPool pool : lootTable.pools) {
            LootCondition[] conditions = pool.conditions;
            List<LootConditionResult> parsedConditions = parseLootConditions(conditions,ItemStack.EMPTY,false);
            if (isDirect){
                if (EMILoot.DEBUG) EMILoot.LOGGER.info("Adding direct drop condition to " + currentTable);
                parsedConditions.add(new LootConditionResult(TextKey.of("emi_loot.condition.direct_drop")));
            }
            LootFunction[] functions = pool.functions;
            List<LootFunctionResult> parsedFunctions = new LinkedList<>();
            for (LootFunction function: functions){
                parsedFunctions.add(parseLootFunction(function));
            }
            LootNumberProvider rollProvider = pool.rolls;
            float rollAvg = NumberProcessors.getRollAvg(rollProvider);
            MobLootPoolBuilder builder = new MobLootPoolBuilder(rollAvg, parsedConditions, parsedFunctions);
            LootPoolEntry[] entries = pool.entries;
            for (LootPoolEntry entry : entries) {
                parseLootPoolEntry(builder,entry);
            }
            sender.addBuilder(builder);
        }
    }

    private static GameplayLootTableSender parseGameplayLootTable(LootTable lootTable, Identifier id){
        GameplayLootTableSender sender = new GameplayLootTableSender(id);
        for (LootPool pool : lootTable.pools) {
            LootCondition[] conditions = pool.conditions;
            List<LootConditionResult> parsedConditions = parseLootConditions(conditions,ItemStack.EMPTY,false);
            LootFunction[] functions = pool.functions;
            List<LootFunctionResult> parsedFunctions = new LinkedList<>();
            for (LootFunction function: functions){
                parsedFunctions.add(parseLootFunction(function));
            }
            LootNumberProvider rollProvider = pool.rolls;
            float rollAvg = NumberProcessors.getRollAvg(rollProvider);
            GameplayLootPoolBuilder builder = new GameplayLootPoolBuilder(rollAvg,parsedConditions,parsedFunctions);
            LootPoolEntry[] entries = pool.entries;
            for (LootPoolEntry entry : entries) {
                parseLootPoolEntry(builder,entry);
            }
            sender.addBuilder(builder);
        }
        return sender;
    }


    private static ArchaeologyLootTableSender parseArchaeologyTable(LootTable lootTable, Identifier id) {
        ArchaeologyLootTableSender sender = new ArchaeologyLootTableSender(id);
        for (LootPool pool : lootTable.pools) {
            LootNumberProvider rollProvider = pool.rolls;
            float rollAvg = NumberProcessors.getRollAvg(rollProvider);
            ArchaeologyLootPoolBuilder builder = new ArchaeologyLootPoolBuilder(rollAvg);
            LootPoolEntry[] entries = pool.entries;
            for (LootPoolEntry entry : entries) {
                parseLootPoolEntry(builder, entry);
            }
            sender.addBuilder(builder);
        }
        return sender;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static void parseLootPoolEntry(LootBuilder builder, LootPoolEntry entry){
        parseLootPoolEntry(builder, entry,null);
    }

    static void parseLootPoolEntry(LootBuilder builder, LootPoolEntry entry, @Nullable PostProcessor process){
        if(entry instanceof TagEntry tagEntry){
            if (process == PostProcessor.TAG){
                List<ItemEntryResult> result = parseTagEntry(tagEntry, false);
                result.forEach(builder::addItem);
            } else {
                builder.addEntryForPostProcessing(PostProcessor.TAG,tagEntry);
            }
        } else {
            List<ItemEntryResult> result = parseLootPoolEntry(entry,false);
            result.forEach(builder::addItem);
        }
    }

    static List<ItemEntryResult> parseLootPoolEntry(LootPoolEntry entry, boolean parentIsAlternative){
        if (entry instanceof ItemEntry itemEntry) {
            return parseItemEntry(itemEntry, parentIsAlternative);
        } else if(entry instanceof AlternativeEntry alternativeEntry){
            return parseAlternativeEntry(alternativeEntry);
        }else if(entry instanceof GroupEntry groupEntry){
            return parseGroupEntry(groupEntry, parentIsAlternative);
        }else if(entry instanceof SequenceEntry sequenceEntry){
            return parseSequenceEntry(sequenceEntry, parentIsAlternative);
        } else if(entry instanceof TagEntry tagEntry){
            return  parseTagEntry(tagEntry, parentIsAlternative);
        } else if (entry instanceof LootTableEntry lootTableEntry){
            return parseLootTableEntry(lootTableEntry, parentIsAlternative);
        }
        return List.of();
    }

    static List<ItemEntryResult> parseItemEntry(ItemEntry entry, boolean parentIsAlternative){
        int weight = ((LeafEntryAccessor) entry).getWeight();
        ItemStack item = new ItemStack(((ItemEntryAccessor) entry).getItem());
        LootFunction[] functions = ((LeafEntryAccessor) entry).getFunctions();
        LootCondition[] conditions = ((LootPoolEntryAccessor) entry).getConditions();
        return parseItemEntry(weight, item, functions, conditions, parentIsAlternative);
    }
    
    static List<ItemEntryResult> parseItemEntry(int weight, ItemStack item, LootFunction[] functions, LootCondition[] conditions, boolean parentIsAlternative){
        FunctionApplierResult functionApplierResult = applyLootFunctionToItem(functions,item,weight,parentIsAlternative);
        List<ItemEntryResult> conditionalEntryResults = functionApplierResult.conditionalResults;
        List<TextKey> functionTexts = functionApplierResult.functionTexts;
        item = functionApplierResult.stack;
        List<TextKey> conditionsTexts = parseLootConditionTexts(conditions,item,parentIsAlternative);
        List<ItemEntryResult> returnList = new LinkedList<>();
        returnList.add(new ItemEntryResult(item,weight,conditionsTexts,functionTexts));
        conditionalEntryResults.forEach(conditionalEntry->{
            conditionalEntry.conditions.addAll(conditionsTexts);
            conditionalEntry.functions.addAll(functionTexts);
            returnList.add(conditionalEntry);
        });

        return returnList;
    }

    static List<ItemEntryResult> parseTagEntry(TagEntry entry, boolean parentIsAlternative){
        TagKey<Item> items = ((TagEntryAccessor) entry).getName();
        if (EMILoot.DEBUG) EMILoot.LOGGER.info(">>> Parsing tag entry " + items.id());
        Iterable<RegistryEntry<Item>> itemsItr = Registries.ITEM.iterateEntries(items);
        List<ItemEntryResult> returnList = new LinkedList<>();
        //if (EMILoot.DEBUG) EMILoot.LOGGER.info(itemsItr.toString());
        int weight = ((LeafEntryAccessor) entry).getWeight();
        LootFunction[] functions = ((LeafEntryAccessor) entry).getFunctions();
        LootCondition[] conditions = ((LootPoolEntryAccessor) entry).getConditions();
        for (RegistryEntry<Item> item : itemsItr){
            ItemStack stack = new ItemStack(item.value());
            //if (EMILoot.DEBUG) EMILoot.LOGGER.info("> Stack: " + stack.getName());
            returnList.addAll(parseItemEntry(weight, stack, functions, conditions, parentIsAlternative));
        }
        return returnList;
        
    }

    static List<ItemEntryResult> parseAlternativeEntry(AlternativeEntry entry){
        LootPoolEntry[] children = ((CombinedEntryAccessor)entry).getChildren();
        LootCondition[] conditions = ((LootPoolEntryAccessor) entry).getConditions();
        List<TextKey> conditionsTexts = parseLootConditionTexts(conditions,ItemStack.EMPTY,true);
        List<ItemEntryResult> results = new LinkedList<>();
        Arrays.stream(children).forEach((lootEntry)->{
            List<ItemEntryResult> result = parseLootPoolEntry(lootEntry,true);
            result.forEach(resultEntry ->{
                resultEntry.conditions.addAll(conditionsTexts);
                results.add(resultEntry);
            });
        });
        return results;
    }

    static List<ItemEntryResult> parseGroupEntry(GroupEntry entry, boolean parentIsAlternative){
        LootPoolEntry[] children = ((CombinedEntryAccessor)entry).getChildren();
        LootCondition[] conditions = ((LootPoolEntryAccessor) entry).getConditions();
        List<TextKey> conditionsTexts = parseLootConditionTexts(conditions,ItemStack.EMPTY,parentIsAlternative);
        List<ItemEntryResult> results = new LinkedList<>();
        Arrays.stream(children).forEach((lootEntry)->{
            List<ItemEntryResult> result = parseLootPoolEntry(lootEntry,parentIsAlternative);
            result.forEach(resultEntry ->{
                resultEntry.conditions.addAll(conditionsTexts);
                results.add(resultEntry);
            });
        });
        return results;
    }

    static List<ItemEntryResult> parseSequenceEntry(SequenceEntry entry, boolean parentIsAlternative){
        LootPoolEntry[] children = ((CombinedEntryAccessor)entry).getChildren();
        LootCondition[] conditions = ((LootPoolEntryAccessor) entry).getConditions();
        List<TextKey> conditionsTexts = parseLootConditionTexts(conditions,ItemStack.EMPTY,parentIsAlternative);
        List<ItemEntryResult> results = new LinkedList<>();
        TextKey sequenceCondition = TextKey.of("emi_loot.condition.sequence");
        Arrays.stream(children).forEach((lootEntry)->{
            List<ItemEntryResult> result = parseLootPoolEntry(lootEntry,parentIsAlternative);
            result.forEach(resultEntry ->{
                resultEntry.conditions.addAll(conditionsTexts);
                resultEntry.conditions.add(sequenceCondition);
                results.add(resultEntry);
            });
        });
        return results;
    }

    static List<ItemEntryResult> parseLootTableEntry(LootTableEntry entry, boolean parentIsAlternative){
        Identifier id = ((LootTableEntryAccessor)entry).getId();
        if (LootTableParser.keyLookUp.containsKey(id)) {
            if (LootTableParser.tables.containsKey(keyLookUp.get(id))) {
                Object temp = LootTableParser.tables.get(keyLookUp.get(id));
                if (!(temp instanceof LootTable table)) return List.of();
                LootContextType type = table.getType();
                LootSender<?> results;
                LootCondition[] conditions = ((LootPoolEntryAccessor) entry).getConditions();
                List<TextKey> conditionsTexts = parseLootConditionTexts(conditions, ItemStack.EMPTY, parentIsAlternative);
                if (type == LootContextTypes.CHEST) {
                    results = parseChestLootTable(table, id);
                } else if (type == LootContextTypes.BLOCK) {
                    results = parseBlockLootTable(table, id);
                } else if (type == LootContextTypes.ENTITY) {
                    results = parseMobLootTable(table, id, new Identifier("empty"));
                } else if (type == LootContextTypes.FISHING) {
                    results = parseGameplayLootTable(table, id);
                } else {
                    results = new EmptyLootTableSender();
                }
                List<? extends LootBuilder> parsedBuilders = results.getBuilders();
                List<ItemEntryResult> parsedList = new LinkedList<>();
                parsedBuilders.forEach(parsedBuilder ->
                        parsedList.addAll(parsedBuilder.revert())
                );
                parsedList.forEach(result -> result.conditions.addAll(conditionsTexts));
                LootFunction[] functions = ((LeafEntryAccessor) entry).getFunctions();
                return applyLootFunctionsToTableResults(functions, parsedList, parentIsAlternative);
            }
        }
        return List.of();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    static LootFunctionResult parseLootFunction(LootFunction function){
        return parseLootFunction(function, ItemStack.EMPTY,false);
    }

    static LootFunctionResult parseLootFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative){
        LootFunctionType type;
        try {
            type = function.getType();
        } catch (Exception e){
            EMILoot.LOGGER.error("failed to determine a function type for stack " + stack.getName() + " in table " + currentTable);
            e.printStackTrace();
            return LootFunctionResult.EMPTY;
        }
        List<TextKey> conditionsTexts;
        if (function instanceof ConditionalLootFunction){
            LootCondition[] conditions = ((ConditionalLootFunctionAccessor)function).getConditions();
            conditionsTexts = parseLootConditionTexts(conditions,stack,parentIsAlternative);
        } else {
            conditionsTexts = new LinkedList<>();
        }
        try {
            return LootParserRegistry.parseFunction(function,stack,type,parentIsAlternative,conditionsTexts);
        } catch(Exception e){
            EMILoot.LOGGER.error("Failed to parse LootCondition of type " + type + " for stack " + stack.getName() + " in table " + currentTable);
            e.printStackTrace();
            return LootFunctionResult.EMPTY;
        }
    }

    private static List<ItemEntryResult> applyLootFunctionsToTableResults(LootFunction[] functions, List<ItemEntryResult> parsedList, boolean parentIsAlternative){
        List<ItemEntryResult> conditionalEntryResults = new LinkedList<>();
        List<ItemEntryResult> processedEntryResults = new LinkedList<>();
        parsedList.forEach(itemEntry -> {
            FunctionApplierResult result = applyLootFunctionToItem(functions,itemEntry.item, itemEntry.weight, parentIsAlternative);
            List<TextKey> conditionTexts = itemEntry.conditions;
            List<TextKey> functionTexts = itemEntry.functions;
            functionTexts.addAll(result.functionTexts);
            processedEntryResults.add(new ItemEntryResult(result.stack, itemEntry.weight, conditionTexts,functionTexts));
            conditionalEntryResults.addAll(result.conditionalResults);
        });
        processedEntryResults.addAll(conditionalEntryResults);
        return processedEntryResults;
    }

    private static FunctionApplierResult applyLootFunctionToItem(LootFunction[] functions, ItemStack item, int weight, boolean parentIsAlternative){
        List<TextKey> functionTexts = new LinkedList<>();
        List<ItemEntryResult> conditionalEntryResults = new LinkedList<>();
        for (LootFunction lootFunction : functions) {
            LootFunctionResult result = parseLootFunction(lootFunction, item, parentIsAlternative);
            TextKey lootText = result.text;
            ItemStack newStack = result.stack;
            List<TextKey> resultConditions = result.conditions;

            if (!resultConditions.isEmpty()){
                ItemStack conditionalItem;
                if (newStack != ItemStack.EMPTY){
                    conditionalItem = newStack;
                } else {
                    conditionalItem = item;
                }
                List<TextKey> conditionalFunctionTexts = new LinkedList<>();
                conditionalFunctionTexts.add(lootText);
                conditionalEntryResults.add(new ItemEntryResult(conditionalItem,weight,resultConditions,conditionalFunctionTexts));
            } else {
                if (lootText.isNotEmpty()) {
                    functionTexts.add(lootText);
                }
                if (newStack != ItemStack.EMPTY) {
                    item = newStack;
                }
            }
        }
        return new FunctionApplierResult(conditionalEntryResults,functionTexts,item);
    }
    
    ///////////////////////////////////////////////////////////////

    public static List<TextKey> parseLootConditionTexts(LootCondition[] conditions, ItemStack item, boolean parentIsAlternative){
        List<TextKey> conditionsTexts = new LinkedList<>();
        List<LootConditionResult> parsedConditions = parseLootConditions(conditions, item, parentIsAlternative);
        for (LootConditionResult result: parsedConditions){
                    conditionsTexts.add(result.text);
        }
        return conditionsTexts;
    }

    public static List<LootConditionResult> parseLootConditions(LootCondition[] conditions, ItemStack item, boolean parentIsAlternative){
        List<LootConditionResult> parsedConditions = new LinkedList<>();
        for (LootCondition condition: conditions){
            List<LootConditionResult> results = parseLootCondition(condition, item, parentIsAlternative);
            for (LootConditionResult result: results){
                if (result.text.isNotEmpty()){
                    parsedConditions.add(result);
                }
            }
        }
        return parsedConditions;
    }

    public static List<LootConditionResult> parseLootCondition(LootCondition condition, ItemStack stack){
        return parseLootCondition(condition, stack, false);
    }

    public static List<LootConditionResult> parseLootCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        LootConditionType type;
        try {
            type = condition.getType();
        } catch (Exception e){
            EMILoot.LOGGER.error("failed to determine a condition type for stack " + stack.getName() + " in table " + currentTable);
            return Collections.singletonList(LootConditionResult.EMPTY);
        }
        try {
            return LootParserRegistry.parseCondition(condition,type,stack,parentIsAlternative);
        } catch (Exception e){
            EMILoot.LOGGER.error("Failed to parse LootCondition of type " + condition.getType() + " for stack " + stack.getName() + " in table " + currentTable);
            e.printStackTrace();
            return Collections.singletonList(LootConditionResult.EMPTY);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Text compileConditionTexts(ItemStack stack,List<LootConditionResult> results){
        MutableText finalText = LText.empty();
        int size = results.size();
        for(int i = 0; i < size;i++){
            LootConditionResult result = results.get(i);
            Text resultText = result.text.process(stack,null).text();
            if (i == 0){
                finalText = resultText.copy();
            } else {
                finalText.append(resultText);
            }
            if (i<(size - 1)){
                finalText.append(LText.translatable("emi_loot.and"));
            }
        }
        return finalText;
    }

    public enum PostProcessor{
        TAG
    }

    public record FunctionApplierResult(List<ItemEntryResult> conditionalResults, List<TextKey> functionTexts, ItemStack stack){}

    public record LootFunctionResult(
            TextKey text,
            ItemStack stack,
            List<TextKey> conditions
    ){
        public static LootFunctionResult EMPTY = new LootFunctionResult(TextKey.empty(), ItemStack.EMPTY, new LinkedList<>());
    }

    public record LootConditionResult(
            TextKey text
    ){
        public static LootConditionResult EMPTY = new LootConditionResult(TextKey.empty());

        public TextKey getText(){
            return text;
        }
    }

    public record ItemEntryResult(
            ItemStack item,
            int weight,
            List<TextKey> conditions,
            List<TextKey> functions
    ){}
}
