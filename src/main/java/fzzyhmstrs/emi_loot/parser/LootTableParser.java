package fzzyhmstrs.emi_loot.parser;

import com.google.common.collect.Multimap;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.*;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.parser.registry.LootParserRegistry;
import fzzyhmstrs.emi_loot.server.*;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.LootManagerConditionManager;
import fzzyhmstrs.emi_loot.util.TextKey;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
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
    public static final Object2BooleanMap<PostProcessor> postProcessors;
    private static Map<Identifier, LootTable> tables = new HashMap<>();
    public static LootConditionManager conditionManager;
    public static String currentTable = "none";
    public static List<Identifier> parsedDirectDrops = new LinkedList<>();
    public static boolean hasParsedLootTables = false;


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
        if (!hasPostProcessed()){
            EMILoot.LOGGER.warn("Post-processing not completed for some reason, completing now...");
            for (PostProcessor process: PostProcessor.values()){
                postProcess(process);
            }
            EMILoot.LOGGER.warn("Post-processing complete!");
        }
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->{
            if (EMILoot.config.parseChestLoot)
                chestSenders.forEach((id,chestSender) -> chestSender.send(handler.player));
            if (EMILoot.config.parseBlockLoot)
                blockSenders.forEach((id,blockSender) -> blockSender.send(handler.player));
            if (EMILoot.config.parseMobLoot)
                mobSenders.forEach((id,mobSender) -> mobSender.send(handler.player));
            if (EMILoot.config.parseGameplayLoot)
                gameplaySenders.forEach((id,gameplaySender) -> gameplaySender.send(handler.player));

        });
    }

    public static void parseLootTables(LootManager manager, Map<Identifier, LootTable> tables) {
        LootTableParser.tables = tables;
        parsedDirectDrops = new LinkedList<>();
        LootTableParser.conditionManager = ((LootManagerConditionManager)manager).getManager();
        EMILoot.LOGGER.info("parsing loot tables");
        tables.forEach(LootTableParser::parseLootTable);
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
        currentTable = id.toString();
        LootContextType type = lootTable.getType();
        if (type == LootContextTypes.CHEST && EMILoot.config.parseChestLoot) {
            chestSenders.put(id, parseChestLootTable(lootTable,id));
        } else if (type == LootContextTypes.BLOCK && EMILoot.config.parseBlockLoot) {
            blockSenders.put(id, parseBlockLootTable(lootTable,id));
        } else if ((type == LootContextTypes.FISHING || type == LootContextTypes.GIFT ||type == LootContextTypes.BARTER) && EMILoot.config.parseGameplayLoot){
            if (Objects.equals(id.getNamespace(), "minecraft") && Objects.equals(id.getPath(), "gameplay/fishing")) return;
            gameplaySenders.put(id, parseGameplayLootTable(lootTable, id));
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
        postProcessors.put(process,true);
    }

    private static void parseEntityType(LootManager manager,EntityType<?> type, Identifier mobTableId, Identifier fallback){
        Identifier mobId = Registries.ENTITY_TYPE.getId(type);
        LootTable mobTable = manager.getTable(mobTableId);
        if (type == EntityType.PIG && mobId.equals(fallback) || mobTable != LootTable.EMPTY) {
            currentTable = mobTableId.toString();
            mobSenders.put(mobTableId, parseMobLootTable(mobTable, mobTableId, mobId));
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
            List<LootConditionResult> parsedConditions = new LinkedList<>();
            if (isDirect){
                if (EMILoot.DEBUG) EMILoot.LOGGER.info("Adding direct drop condition to " + currentTable);
                parsedConditions.add(new LootConditionResult(TextKey.of("emi_loot.condition.direct_drop")));
            }
            for (LootCondition condition: conditions){
                List<LootConditionResult> results = parseLootCondition(condition, ItemStack.EMPTY);
                for (LootConditionResult result: results){
                    if (result.text.isNotEmpty()){
                        parsedConditions.add(result);
                    }
                }
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
            List<LootConditionResult> parsedConditions = new LinkedList<>();
            if (isDirect){
                if (EMILoot.DEBUG) EMILoot.LOGGER.info("Adding direct drop condition to " + currentTable);
                parsedConditions.add(new LootConditionResult(TextKey.of("emi_loot.condition.direct_drop")));
            }
            for (LootCondition condition: conditions){
                List<LootConditionResult> results = parseLootCondition(condition, ItemStack.EMPTY);
                for (LootConditionResult result: results){
                    if (result.text.isNotEmpty()){
                        parsedConditions.add(result);
                    }
                }
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
            List<LootConditionResult> parsedConditions = new LinkedList<>();
            for (LootCondition condition: conditions){
                List<LootConditionResult> results = parseLootCondition(condition, ItemStack.EMPTY);
                for (LootConditionResult result: results){
                    if (result.text.isNotEmpty()){
                        parsedConditions.add(result);
                    }
                }
            }
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static void parseLootPoolEntry(LootBuilder builder, LootPoolEntry entry){
        parseLootPoolEntry(builder, entry,null);
    }

    static void parseLootPoolEntry(LootBuilder builder, LootPoolEntry entry, @Nullable PostProcessor process){
        if (entry instanceof ItemEntry itemEntry) {
            List<ItemEntryResult> result = parseItemEntry(itemEntry, false);
            result.forEach(builder::addItem);
        } else if(entry instanceof AlternativeEntry alternativeEntry){
            List<ItemEntryResult> result = parseAlternativeEntry(alternativeEntry, false);
            result.forEach(builder::addItem);
        } else if(entry instanceof TagEntry tagEntry){
            if (process == PostProcessor.TAG){
                List<ItemEntryResult> result = parseTagEntry(tagEntry);
                result.forEach(builder::addItem);
            } else {
                builder.addEntryForPostProcessing(PostProcessor.TAG,tagEntry);
            }
        } else if (entry instanceof LootTableEntry lootTableEntry){
            LootSender<?> results = parseLootTableEntry(lootTableEntry, false);
            List<? extends LootBuilder> parsedBuilders = results.getBuilders();
            List<ItemEntryResult> parsedList = new LinkedList<>();
            parsedBuilders.forEach(parsedBuilder->
                    parsedList.addAll(parsedBuilder.revert())
            );
            parsedList.forEach(builder::addItem);
        }
    }

    static List<ItemEntryResult> parseItemEntry(ItemEntry entry, boolean skipExtras){
        return parseItemEntry(entry, skipExtras, false);
    }

    static List<ItemEntryResult> parseItemEntry(ItemEntry entry, boolean skipExtras, boolean parentIsAlternative){
        int weight = ((LeafEntryAccessor) entry).getWeight();
        ItemStack item = new ItemStack(((ItemEntryAccessor) entry).getItem());
        if (skipExtras){
            return Collections.singletonList(new ItemEntryResult(item,weight,new LinkedList<>(), new LinkedList<>()));
        }
        LootFunction[] functions = ((LeafEntryAccessor) entry).getFunctions();
        LootCondition[] conditions = ((LootPoolEntryAccessor) entry).getConditions();
        return parseItemEntry(weight, item, functions, conditions, parentIsAlternative);
    }
    
    static List<ItemEntryResult> parseItemEntry(int weight, ItemStack item, LootFunction[] functions, LootCondition[] conditions, boolean parentIsAlternative){
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
        List<TextKey> conditionsTexts = new LinkedList<>();
        for (LootCondition condition: conditions){
            List<LootConditionResult> results = parseLootCondition(condition,item);
            results.forEach((result)->{
                TextKey lootText = result.text;
                if (lootText.isNotEmpty()){
                    conditionsTexts.add(lootText);
                }
            });
        }
        List<ItemEntryResult> returnList = new LinkedList<>();
        returnList.add(new ItemEntryResult(item,weight,conditionsTexts,functionTexts));
        conditionalEntryResults.forEach(conditionalEntry->{
            conditionalEntry.conditions.addAll(conditionsTexts);
            conditionalEntry.functions.addAll(functionTexts);
            returnList.add(conditionalEntry);
        });

        return returnList;
    }
    
    static List<ItemEntryResult> parseTagEntry(TagEntry entry){
        return parseTagEntry(entry, false);
    }

    static List<ItemEntryResult> parseTagEntry(TagEntry entry, boolean parentIsAlternative){
        TagKey<Item> items = ((TagEntryAccessor) entry).getName();
        if (EMILoot.DEBUG) EMILoot.LOGGER.info(">>> Parsing tag entry " + items.id());
        Iterable<RegistryEntry<Item>> itemsItr = Registries.ITEM.iterateEntries(items);
        List<ItemEntryResult> returnList = new LinkedList<>();
        if (EMILoot.DEBUG) EMILoot.LOGGER.info(itemsItr.toString());
        int weight = ((LeafEntryAccessor) entry).getWeight();
        LootFunction[] functions = ((LeafEntryAccessor) entry).getFunctions();
        LootCondition[] conditions = ((LootPoolEntryAccessor) entry).getConditions();
        for (RegistryEntry<Item> item : itemsItr){
            ItemStack stack = new ItemStack(item.value());
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("> Stack: " + stack.getName());
            returnList.addAll(parseItemEntry(weight, stack, functions, conditions, parentIsAlternative));
        }
        return returnList;
        
    }

    static List<ItemEntryResult> parseAlternativeEntry(AlternativeEntry entry, boolean skipExtra){
        LootPoolEntry[] children = ((CombinedEntryAccessor)entry).getChildren();
        LootCondition[] conditions = ((LootPoolEntryAccessor) entry).getConditions();
        List<TextKey> conditionsTexts = new LinkedList<>();
        for (LootCondition condition: conditions){
            List<LootConditionResult> results = parseLootCondition(condition,ItemStack.EMPTY, true);
            results.forEach((result)->{
                TextKey lootText = result.text;
                if (lootText.isNotEmpty()){
                    conditionsTexts.add(lootText);
                }
            });
        }
        List<ItemEntryResult> results = new LinkedList<>();
        Arrays.stream(children).toList().forEach((lootEntry)->{
            if(lootEntry instanceof ItemEntry itemEntry){
                List<ItemEntryResult> result = parseItemEntry(itemEntry,false, true);
                result.forEach(resultEntry ->{
                    resultEntry.conditions.addAll(conditionsTexts);
                    results.add(resultEntry);
                });
            } else if(lootEntry instanceof TagEntry tagEntry) {
                List<ItemEntryResult> result = parseTagEntry(tagEntry, true);
                result.forEach(resultEntry ->{
                    resultEntry.conditions.addAll(conditionsTexts);
                    results.add(resultEntry);
                });
            } else if(lootEntry instanceof AlternativeEntry alternativeEntry){
                List<ItemEntryResult> altResults = parseAlternativeEntry(alternativeEntry,false);
                altResults.forEach(result-> result.conditions.addAll(conditionsTexts));
                results.addAll(altResults);
            }
        });
        return results;
    }

    static LootSender<?> parseLootTableEntry(LootTableEntry entry, boolean skipExtras){
        Identifier id = ((LootTableEntryAccessor)entry).getId();
        if (LootTableParser.tables.containsKey(id)) {
            LootTable table = LootTableParser.tables.get(id);
            LootContextType type = table.getType();
            if (type == LootContextTypes.CHEST) {
                return parseChestLootTable(table,id);
            } else if (type == LootContextTypes.BLOCK) {
                return parseBlockLootTable(table,id);
            } else if (type == LootContextTypes.ENTITY) {
                return parseMobLootTable(table,id, new Identifier("empty"));
            } else if (type == LootContextTypes.FISHING) {
                return parseGameplayLootTable(table,id);
            }
        }
        return new EmptyLootTableSender();
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
            EMILoot.LOGGER.error(Arrays.toString(e.getStackTrace()));
            return LootFunctionResult.EMPTY;
        }
        List<TextKey> conditionsTexts = new LinkedList<>();
        if (function instanceof ConditionalLootFunction){
            LootCondition[] conditions = ((ConditionalLootFunctionAccessor)function).getConditions();
            for(LootCondition condition: conditions){
                List<LootConditionResult> results = parseLootCondition(condition,stack);
                results.forEach((result)->{
                    TextKey lootText = result.text;
                    if (lootText.isNotEmpty()){
                        conditionsTexts.add(lootText);
                    }
                });
            }
        }
        return LootParserRegistry.parseFunction(function,stack,type,parentIsAlternative,conditionsTexts);
    }
    
    ///////////////////////////////////////////////////////////////

    public static List<LootConditionResult> parseLootCondition(LootCondition condition, ItemStack stack){
        return parseLootCondition(condition, stack, false);
    }

    public static List<LootConditionResult> parseLootCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        LootConditionType type;
        try {
            type = condition.getType();
        } catch (Exception e){
            EMILoot.LOGGER.error("failed to determine a loot type for stack " + stack.getName() + " in table " + currentTable);
            return Collections.singletonList(LootConditionResult.EMPTY);
        }
        return LootParserRegistry.parseCondition(condition,type,stack,parentIsAlternative);
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
