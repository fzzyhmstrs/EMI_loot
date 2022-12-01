package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.*;
import fzzyhmstrs.emi_loot.server.*;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
ipmort net.minecraft.loot.context.LootContext.EntityTarget;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.Structure;

import java.util.*;

public class LootTableParser {

    private static final Map<Identifier, ChestLootTableSender> chestSenders = new HashMap<>();
    private static final Map<Identifier, BlockLootTableSender> blockSenders = new HashMap<>();
    private static final Map<Identifier, MobLootTableSender> mobSenders = new HashMap<>();
    private static Map<Identifier, LootTable> tables = new HashMap<>();

    public void registerServer(){
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> chestSenders.forEach((id,chestSender) -> chestSender.send(handler.player)));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> blockSenders.forEach((id,blockSender) -> blockSender.send(handler.player)));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> mobSenders.forEach((id,mobSender) -> mobSender.send(handler.player)));
    }

    public static void parseLootTables(LootManager manager, Map<Identifier, LootTable> tables) {
        LootTableParser.tables = tables;
        System.out.println("parsing loot tables");
        tables.forEach((id,lootTable)-> {
            LootContextType type = lootTable.getType();
            if (type == LootContextTypes.CHEST) {
                chestSenders.put(id, parseChestLootTable(lootTable,id));
            } else if (type == LootContextTypes.BLOCK) {
                blockSenders.put(id, parseBlockLootTable(lootTable,id));
            }
        });
        Identifier chk = new Identifier("pig");
        Registry.ENTITY_TYPE.stream().toList().forEach((type)->{
            Identifier mobId = Registry.ENTITY_TYPE.getId(type);
            Identifier mobTableId = type.getLootTableId();
            LootTable mobTable = manager.getTable(mobTableId);
            if (type == EntityType.PIG && mobId.equals(chk) || mobTable != LootTable.EMPTY){
                mobSenders.put(mobTableId,parseMobLootTable(mobTable,mobTableId,mobId));
            }
        });
        System.out.println("finished parsing loot tables");
    }

    private static ChestLootTableSender parseChestLootTable(LootTable lootTable, Identifier id){
        ChestLootTableSender sender = new ChestLootTableSender(id);
        for (LootPool pool : lootTable.pools) {
            LootNumberProvider rollProvider = pool.rolls;
            float rollAvg = getRollAvg(rollProvider);
            ChestLootPoolBuilder builder = new ChestLootPoolBuilder(rollAvg);
            LootPoolEntry[] entries = pool.entries;
            for (LootPoolEntry entry : entries) {
                if (entry instanceof ItemEntry itemEntry) {
                    List<ItemEntryResult> result = parseItemEntry(itemEntry, false);
                    result.forEach(resultEntry -> builder.addItem(resultEntry.item, resultEntry.weight));
                }
            }
            sender.addBuilder(builder);
        }
        return sender;
    }

    private static BlockLootTableSender parseBlockLootTable(LootTable lootTable, Identifier id){
        BlockLootTableSender sender = new BlockLootTableSender(id);
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
                parsedFunctions.add(parseLootFunction(function,ItemStack.EMPTY));
            }
            LootNumberProvider rollProvider = pool.rolls;
            float rollAvg = getRollAvg(rollProvider);
            BlockLootPoolBuilder builder = new BlockLootPoolBuilder(rollAvg, parsedConditions, parsedFunctions);
            LootPoolEntry[] entries = pool.entries;
            for (LootPoolEntry entry : entries) {
                if (entry instanceof ItemEntry itemEntry) {
                    List<ItemEntryResult> result = parseItemEntry(itemEntry, false);
                    result.forEach(builder::addItem);
                } else if(entry instanceof AlternativeEntry alternativeEntry){
                    List<ItemEntryResult> resultList = parseAlternativeEntry(alternativeEntry,false);
                    for (ItemEntryResult res: resultList){
                        builder.addItem(res);
                    }
                }
            }
            sender.addBuilder(builder);
        }
        return sender;
    }

    private static MobLootTableSender parseMobLootTable(LootTable lootTable, Identifier id, Identifier mobId){
        MobLootTableSender sender = new MobLootTableSender(id, mobId);
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
                parsedFunctions.add(parseLootFunction(function,ItemStack.EMPTY));
            }
            LootNumberProvider rollProvider = pool.rolls;
            float rollAvg = getRollAvg(rollProvider);
            MobLootPoolBuilder builder = new MobLootPoolBuilder(rollAvg, parsedConditions, parsedFunctions);
            LootPoolEntry[] entries = pool.entries;
            for (LootPoolEntry entry : entries) {
                if (entry instanceof ItemEntry itemEntry) {
                    List<ItemEntryResult> result = parseItemEntry(itemEntry, false);
                    result.forEach(builder::addItem);
                } else if(entry instanceof AlternativeEntry alternativeEntry){
                    List<ItemEntryResult> resultList = parseAlternativeEntry(alternativeEntry,false);
                    for (ItemEntryResult res: resultList){
                        builder.addItem(res);
                    }
                } else if (entry instanceof LootTableEntry lootTableEntry){
                    LootSender<?> results = parseLootTableEntry(lootTableEntry,false);
                    List<? extends LootBuilder> parsedBuilders = results.getBuilders();
                    List<ItemEntryResult> parsedList = new LinkedList<>();
                    parsedBuilders.forEach(parsedBuilder->
                        parsedList.addAll(parsedBuilder.revert())
                    );
                    for (ItemEntryResult result: parsedList){
                        builder.addItem(result);
                    }
                }
            }
            sender.addBuilder(builder);
        }
        return sender;
    }

    private static FishingLootTableSender parseFishingLootTable(LootTable lootTable, Identifier id){
        FishingLootTableSender sender = new FishingLootTableSender(id);
        for (LootPool pool : lootTable.pools) {
            LootNumberProvider rollProvider = pool.rolls;
            float rollAvg = getRollAvg(rollProvider);
            ChestLootPoolBuilder builder = new ChestLootPoolBuilder(rollAvg);
            LootPoolEntry[] entries = pool.entries;
            for (LootPoolEntry entry : entries) {
                if (entry instanceof ItemEntry itemEntry) {
                    List<ItemEntryResult> result = parseItemEntry(itemEntry, false);
                    result.forEach(resultEntry -> builder.addItem(resultEntry.item, resultEntry.weight));
                }
            }
            sender.addBuilder(builder);
        }
        return sender;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static List<ItemEntryResult> parseItemEntry(ItemEntry entry, boolean skipExtras){
        return parseItemEntry(entry, skipExtras,false);
    }

    static List<ItemEntryResult> parseItemEntry(ItemEntry entry, boolean skipExtras, boolean parentIsAlternative){
        int weight = ((LeafEntryAccessor) entry).getWeight();
        ItemStack item = new ItemStack(((ItemEntryAccessor) entry).getItem());
        if (skipExtras){
            return Collections.singletonList(new ItemEntryResult(item,weight,new LinkedList<>(), new LinkedList<>()));
        }
        LootFunction[] functions = ((LeafEntryAccessor) entry).getFunctions();
        LootCondition[] conditions = ((LootPoolEntryAccessor) entry).getConditions();
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
                return parseFishingLootTable(table,id);
            }
        }
        return new EmptyLootTableSender();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    static LootFunctionResult parseLootFunction(LootFunction function, ItemStack stack){
        return parseLootFunction(function, stack,false);
    }

    static LootFunctionResult parseLootFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative){
        LootFunctionType type;
        try {
            type = function.getType();
        } catch (Exception e){
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
        if (type == LootFunctionTypes.APPLY_BONUS){
            Enchantment enchant = ((ApplyBonusLootFunctionAccessor)function).getEnchantment();
            String name = enchant.getName(1).getString();
            String nTrim;
            if (enchant.getMaxLevel() != 1) {
                nTrim = name.substring(0, name.length() - 2);
            } else {
                nTrim = name;
            }
            return new LootFunctionResult(TextKey.of("emi_loot.function.bonus",nTrim),ItemStack.EMPTY,conditionsTexts);
        } else if (type == LootFunctionTypes.SET_POTION){
            Potion potion = ((SetPotionLootFunctionAccessor)function).getPotion();
            PotionUtil.setPotion(stack, potion);
            Text potionName = LText.translatable(potion.finishTranslationKey(Items.POTION.getTranslationKey() + ".effect."));
            return new LootFunctionResult(TextKey.of("emi_loot.function.potion",potionName.getString()), ItemStack.EMPTY,conditionsTexts);
        } else if (type == LootFunctionTypes.SET_COUNT){
            LootNumberProvider provider = ((SetCountLootFunctionAccessor)function).getCountRange();
            float rollAvg = getRollAvg(provider);
            boolean add = ((SetCountLootFunctionAccessor)function).getAdd();
            if (add){
                stack.setCount(Math.max(stack.getCount() + (int)rollAvg,1));
            } else {
                stack.setCount(Math.max((int)rollAvg,1));
            }
            if (add){
                return new LootFunctionResult(TextKey.of("emi_loot.function.set_count_add"),ItemStack.EMPTY,conditionsTexts);
            }
            return LootFunctionResult.EMPTY;
        }else if (type == LootFunctionTypes.ENCHANT_WITH_LEVELS){
            if (stack.isOf(Items.BOOK)){
                stack = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantedBookItem.addEnchantment(stack,new EnchantmentLevelEntry(EMILoot.RANDOM,1));
                return new LootFunctionResult(TextKey.of("emi_loot.function.randomly_enchanted_book"), stack,conditionsTexts);
            } else {
                stack.addEnchantment(EMILoot.RANDOM,1);
                return new LootFunctionResult(TextKey.of("emi_loot.function.randomly_enchanted_item"), ItemStack.EMPTY,conditionsTexts);
            }
        }else if (type == LootFunctionTypes.ENCHANT_RANDOMLY){
            if (stack.isOf(Items.BOOK)){
                stack = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantedBookItem.addEnchantment(stack,new EnchantmentLevelEntry(EMILoot.RANDOM,1));
                return new LootFunctionResult(TextKey.of("emi_loot.function.randomly_enchanted_book"), stack,conditionsTexts);
            } else {
                stack.addEnchantment(EMILoot.RANDOM,1);
                return new LootFunctionResult(TextKey.of("emi_loot.function.randomly_enchanted_item"), ItemStack.EMPTY,conditionsTexts);
            }
        } else if (type == LootFunctionTypes.SET_ENCHANTMENTS){
            Map<Enchantment,LootNumberProvider> enchantments = ((SetEnchantmentsLootFunctionAccessor)function).getEnchantments();
            boolean add = ((SetEnchantmentsLootFunctionAccessor)function).getAdd();
            if (stack.isOf(Items.BOOK)){
                stack = new ItemStack(Items.ENCHANTED_BOOK);
                ItemStack finalStack = stack;
                enchantments.forEach((enchantment, provider)->{
                    float rollAvg = getRollAvg(provider);
                    EnchantedBookItem.addEnchantment(finalStack, new EnchantmentLevelEntry(enchantment, (int)rollAvg));
                });
                return new LootFunctionResult(TextKey.of("emi_loot.function.set_enchant_book"), finalStack,conditionsTexts);
            } else {
                Map<Enchantment,Integer> finalStackMap = new HashMap<>();
                if (add){
                    Map<Enchantment, Integer> stackMap = EnchantmentHelper.get(stack);
                    enchantments.forEach((enchantment, provider)->{
                        float rollAvg = getRollAvg(provider);
                        finalStackMap.put(enchantment,Math.max(((int)rollAvg) + stackMap.getOrDefault(enchantment,0),0));
                    });
                } else {
                    enchantments.forEach((enchantment, provider)->{
                        float rollAvg = getRollAvg(provider);
                        finalStackMap.put(enchantment,Math.max(((int)rollAvg),0));
                    });
                }
                EnchantmentHelper.set(finalStackMap,stack);
                return new LootFunctionResult(TextKey.of("emi_loot.function.set_enchant_item"), stack, conditionsTexts);
            }
        } else if (type == LootFunctionTypes.FURNACE_SMELT){
            return new LootFunctionResult(TextKey.of("emi_loot.function.smelt"), ItemStack.EMPTY, conditionsTexts);
        } else if (type == LootFunctionTypes.LOOTING_ENCHANT){
            return new LootFunctionResult(TextKey.of("emi_loot.function.looting"), ItemStack.EMPTY, conditionsTexts);
        } else if (type == LootFunctionTypes.EXPLORATION_MAP){
            ItemStack mapStack;
            String typeKey = "emi_loot.map.unknown";
            if (!stack.isOf(Items.MAP)){
                mapStack = stack;
            } else {
                MapIcon.Type decoration = ((ExplorationMapLootFunctionAccessor)function).getDecoration();
                TagKey<Structure> destination = ((ExplorationMapLootFunctionAccessor)function).getDestination();
                mapStack = new ItemStack(Items.FILLED_MAP);
                MapState.addDecorationsNbt(mapStack, BlockPos.ORIGIN,"+",decoration);
                typeKey = "emi_loot.map."+ destination.id().getPath();
            }
            return new LootFunctionResult(TextKey.of("emi_loot.function.map",LText.translatable(typeKey).getString()), mapStack, conditionsTexts);
        } else if (type == LootFunctionTypes.SET_NAME){
            Text text = ((SetNameLootFunctionAccessor)function).getName();
            stack.setCustomName(text);
            return new LootFunctionResult(TextKey.empty(), stack, conditionsTexts);
        } else if (type == LootFunctionTypes.SET_CONTENTS){
            return new LootFunctionResult(TextKey.of("emi_loot.function.set_contents"), ItemStack.EMPTY, conditionsTexts);
        } else if (type == LootFunctionTypes.SET_DAMAGE){
            LootNumberProvider provider = ((SetDamageLootFunctionAccessor)function).getDurabilityRange();
            float rollAvg = getRollAvg(provider);
            boolean add = ((SetDamageLootFunctionAccessor)function).getAdd();
            int md = stack.getMaxDamage();
            float damage;
            if (add){
                int dmg = stack.getDamage();
                damage = MathHelper.clamp(((float )dmg)/md + (rollAvg * md),0,md);
            } else {
                damage = MathHelper.clamp(rollAvg * md,0,md);
            }
            stack.setDamage(MathHelper.floor(damage));
            return new LootFunctionResult(TextKey.of("emi_loot.function.damage",Integer.toString((int)(rollAvg*100))), stack, conditionsTexts);
        } else if (type == LootFunctionTypes.SET_INSTRUMENT){
            TagKey<Instrument> tag = ((SetGoatHornLootFunctionAccessor)function).getInstrumentTag();
            GoatHornItem.setRandomInstrumentFromTag(stack,tag,EMILoot.emiLootRandom);
            return new LootFunctionResult(TextKey.empty(), stack, conditionsTexts);
        }else if (type == LootFunctionTypes.COPY_STATE){
            return new LootFunctionResult(TextKey.of("emi_loot.function.copy_state"), ItemStack.EMPTY, conditionsTexts);
        }else if (type == LootFunctionTypes.EXPLOSION_DECAY){
            if (parentIsAlternative) return new LootFunctionResult(TextKey.of("emi_loot.function.decay"), ItemStack.EMPTY, conditionsTexts);
            return new LootFunctionResult(TextKey.empty(), ItemStack.EMPTY, conditionsTexts);
        } else {
            return LootFunctionResult.EMPTY;
        }
    }

    static List<LootConditionResult> parseLootCondition(LootCondition condition, ItemStack stack){
        return parseLootCondition(condition, stack, false);
    }

    static List<LootConditionResult> parseLootCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        LootConditionType type;
        try {
            type = condition.getType();
        } catch (Exception e){
            return Collections.singletonList(LootConditionResult.EMPTY);
        }
        if (type == LootConditionTypes.SURVIVES_EXPLOSION){
            if (parentIsAlternative) return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.survives_explosion")));
            return Collections.singletonList(new LootConditionResult(TextKey.empty()));
        } else if (type == LootConditionTypes.BLOCK_STATE_PROPERTY){
            return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.blockstate")));
        } else if (type == LootConditionTypes.TABLE_BONUS){
            Enchantment enchant = ((TableBonusLootConditionAccessor)condition).getEnchantment();
            String name = enchant.getName(1).getString();
            String nTrim;
            if (enchant.getMaxLevel() != 1) {
                 nTrim = name.substring(0, name.length() - 2);
            } else {
                nTrim = name;
            }
            return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.table_bonus",nTrim)));
        } else if (type == LootConditionTypes.INVERTED){
            LootCondition term = ((InvertedLootConditionAccessor)condition).getCondition();
            List<LootConditionResult> termResults = parseLootCondition(term, stack);
            List<LootConditionResult> finalResults = new LinkedList<>();
            termResults.forEach((result)->{
                Text resultText = result.text.process(stack,null).text();
                finalResults.add(new LootConditionResult(TextKey.of("emi_loot.condition.invert",resultText.getString())));
            });
            return finalResults;
        } else if (type == LootConditionTypes.ALTERNATIVE){
            LootCondition[] terms = ((AlternativeLootConditionAccessor)condition).getConditions();
            int size = terms.length;
            if (size == 1){
                List<LootConditionResult> termResults = parseLootCondition(terms[0], stack);
                Text termText = compileConditionTexts(stack,termResults);
                return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.alternates",termText.getString())));
            } else if (size == 2){
                List<LootConditionResult> termResults1 = parseLootCondition(terms[0], stack);
                List<LootConditionResult> termResults2 = parseLootCondition(terms[1], stack);
                Text termText1 = compileConditionTexts(stack,termResults1);
                Text termText2;
                if (termResults2.size() == 1){
                    TextKey key = termResults2.get(0).text;
                    if (key.args().size() == 1){
                        termText2 = Text.of(key.args().get(0));
                    } else {
                        termText2 = compileConditionTexts(stack,termResults2);
                    }
                } else {
                    termText2 = compileConditionTexts(stack, termResults2);
                }
                List<String> args = new LinkedList<>(Arrays.stream(new String[]{termText1.getString(), termText2.getString()}).toList());
                return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.alternates_2",args)));
            } else {
                List<String> args = new LinkedList<>();
                Arrays.stream(terms).toList().forEach((term)-> {
                    List<LootConditionResult> termResults = parseLootCondition(term, stack);
                    Text termText = compileConditionTexts(stack,termResults);
                    args.add(termText.getString());
                });
                return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.alternates_3",args)));
            }
        } else if (type == LootConditionTypes.KILLED_BY_PLAYER){
            return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.killed_player")));
        } else if (type == LootConditionTypes.RANDOM_CHANCE){
            float chance = ((RandomChanceLootConditionAccessor)condition).getChance();
            return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.chance", Float.toString((chance*100)))));
        } else if (type == LootConditionTypes.RANDOM_CHANCE_WITH_LOOTING){
            float chance = ((RandomChanceWithLootingLootConditionAccessor)condition).getChance();
            float multiplier = ((RandomChanceWithLootingLootConditionAccessor)condition).getLootingMultiplier();
            List<String> args = new LinkedList<>(Arrays.stream(new String[]{Float.toString((chance*100)), Float.toString((multiplier*100))}).toList());
            return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.chance_looting", args)));
        } else if (type == LootConditionTypes.DAMAGE_SOURCE_PROPERTIES){
            DamageSourcePredicate damageSourcePredicate = ((DamageSourcePropertiesLootConditionAccessor)condition).getPredicate();
            Text damageText = DamageSourcePredicateParser.parseDamageSourcePredicate(damageSourcePredicate);
            return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.damage_source",damageText.getString())));
        } else if (type == LootConditionTypes.LOCATION_CHECK){
            return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.location")));
        } else if (type == LootConditionTypes.ENTITY_PROPERTIES){
            LootContext.EntityTarget entity = ((EntityPropertiesLootConditionAccessor)condition).getEntity();
            EntityPredicate predicate = ((EntityPropertiesLootConditionAccessor)condition).getPredicate();
            MutableText propText;
            if (entity == LootContext.EntityTarget.THIS){
                propText = LText.translatable("emi_loot.entity_predicate.entity_this", EntityPredicateParser.parseEntityPredicate(predicate));
            } else {
                propText = LText.translatable("emi_loot.entity_predicate.entity_killer", EntityPredicateParser.parseEntityPredicate(predicate));
            }
            return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.entity_props",propText.getString())));
        } else if (type == LootConditionTypes.MATCH_TOOL){
            ItemPredicate predicate = ((MatchToolLootConditionAccessor)condition).getPredicate();
            Text predicateText = ItemPredicateParser.parseItemPredicate(predicate);
            return Collections.singletonList(new LootConditionResult(TextKey.of("emi_loot.condition.match_tool", predicateText.getString())));
        }
        return Collections.singletonList(LootConditionResult.EMPTY);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static Text compileConditionTexts(ItemStack stack,List<LootConditionResult> results){
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

    private static float getRollAvg(LootNumberProvider provider){
        LootNumberProviderType type = provider.getType();
        if(type == LootNumberProviderTypes.CONSTANT){
            return ((ConstantLootNumberProviderAccessor)provider).getValue();
        } else if(type == LootNumberProviderTypes.BINOMIAL){
            LootNumberProvider n = ((BinomialLootNumberProviderAccessor)provider).getN();
            LootNumberProvider p = ((BinomialLootNumberProviderAccessor)provider).getP();
            float nVal = getRollAvg(n);
            float pVal = getRollAvg(p);
            return nVal * pVal;
        } else if(type == LootNumberProviderTypes.UNIFORM){
            LootNumberProvider min = ((UniformLootNumberProviderAccessor)provider).getMin();
            LootNumberProvider max = ((UniformLootNumberProviderAccessor)provider).getMax();
            float minVal = getRollAvg(min);
            float maxVal = getRollAvg(max);
            return (minVal + maxVal) / 2f;
        } else if (type == LootNumberProviderTypes.SCORE){
            return 0f;
        } else {
            return 0f;
        }
    }

    public record LootFunctionResult(
            TextKey text,
            ItemStack stack,
            List<TextKey> conditions
    ){
        static LootFunctionResult EMPTY = new LootFunctionResult(TextKey.empty(), ItemStack.EMPTY, new LinkedList<>());
    }

    public record LootConditionResult(
            TextKey text
    ){
        static LootConditionResult EMPTY = new LootConditionResult(TextKey.empty());
    }

    public record ItemEntryResult(
            ItemStack item,
            int weight,
            List<TextKey> conditions,
            List<TextKey> functions
    ){}
}
