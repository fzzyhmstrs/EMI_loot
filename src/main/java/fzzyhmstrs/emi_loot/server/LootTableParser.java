package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.mixins.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

public class LootTableParser {

    private static final Map<Identifier, ChestLootTableSender> chestSenders = new HashMap<>();

    public void registerServer(){
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> chestSenders.forEach((id,chestSender) -> chestSender.send(handler.player)));
    }

    public static void parseLootTables(Map<Identifier, LootTable> tables) {
        tables.forEach((id,lootTable)-> {
            LootContextType type = lootTable.getType();
            if (type == LootContextTypes.CHEST) {
                ChestLootTableSender sender = new ChestLootTableSender(id);
                LootPool[] pools = ((LootTableAccessor) lootTable).getPools();
                for (LootPool pool : pools) {
                    LootNumberProvider rollProvider = ((LootPoolAccessor) pool).getRolls();
                    float rollAvg = getRollAvg(rollProvider);
                    ChestLootPoolBuilder builder = new ChestLootPoolBuilder(rollAvg);
                    LootPoolEntry[] entries = ((LootPoolAccessor) pool).getEntries();
                    for (LootPoolEntry entry : entries) {
                        if (entry instanceof ItemEntry itemEntry) {
                            ItemEntryResult result = parseItemEntry(itemEntry, false);
                            builder.addItem(result.item, result.weight);
                        }
                    }
                    sender.addBuilder(builder);
                }
                chestSenders.put(id, sender);
            } else if (type == LootContextTypes.BLOCK) {
                BlockLootTableSender sender = new BlockLootTableSender(id);
                LootPool[] pools = ((LootTableAccessor) lootTable).getPools();
                for (LootPool pool : pools) {
                    LootNumberProvider rollProvider = ((LootPoolAccessor) pool).getRolls();
                    float rollAvg = getRollAvg(rollProvider);
                    BlockLootPoolBuilder builder = new BlockLootPoolBuilder(rollAvg);
                    LootPoolEntry[] entries = ((LootPoolAccessor) pool).getEntries();
                    for (LootPoolEntry entry : entries) {
                        if (entry instanceof ItemEntry itemEntry) {
                            ItemEntryResult result = parseItemEntry(itemEntry, false);
                            //builder.addItem(result.item, result.weight);
                        }
                    }
                    sender.addBuilder(builder);
                }
            }
        });
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

    static ItemEntryResult parseItemEntry(ItemEntry entry, boolean skipExtras){
        int weight = ((LeafEntryAccessor) entry).getWeight();
        ItemStack item = new ItemStack(((ItemEntryAccessor) entry).getItem());
        if (skipExtras){
            return new ItemEntryResult(item,weight,new LinkedList<>(), new LinkedList<>());
        }
        LootFunction[] functions = ((LeafEntryAccessor) entry).getFunctions();
        LootCondition[] conditions = ((LootPoolEntryAccessor) entry).getConditions();
        List<Text> functionTexts = new LinkedList<>();
        Arrays.stream(functions).toList().forEach(lootFunction -> {
            Text lootText = parseLootFunction(lootFunction, item);
            if (!Objects.equals(lootText, Text.empty())){
                functionTexts.add(lootText);
            }
        });
        List<Text> conditionsTexts = new LinkedList<>();
        return new ItemEntryResult(item,weight,conditionsTexts,functionTexts);
    }

    static List<ItemEntryResult> parseAlternativeEntry(AlternativeEntry entry, boolean skipExtra){
        LootPoolEntry[] children = ((CombinedEntryAccessor)entry).getChildren();
        List<ItemEntryResult> results = new LinkedList<>();
        Arrays.stream(children).toList().forEach((lootEntry)->{
            if(lootEntry instanceof ItemEntry itemEntry){
                ItemEntryResult result = parseItemEntry(itemEntry,false);
                results.add(result);
            }
        });
        return results;
    }

    static Text parseLootFunction(LootFunction function, ItemStack stack){
        LootFunctionType type = function.getType();
        if (type == LootFunctionTypes.APPLY_BONUS){
            return Text.translatable("emi_loot.function.bonus");
        } else if (type == LootFunctionTypes.SET_POTION){
            Potion potion = ((SetPotionLootFunctionAccessor)function).getPotion();
            PotionUtil.setPotion(stack, potion);
            System.out.println(stack.getNbt());
            Text potionName = Text.translatable(potion.finishTranslationKey(Items.POTION.getTranslationKey() + ".effect."));
            return Text.translatable("emi_loot.function.potion",potionName);
        } else if (type == LootFunctionTypes.SET_COUNT){
            LootNumberProvider provider = ((SetCountLootFunctionAccessor)function).getCountRange();
            float rollAvg = getRollAvg(provider);
            boolean add = ((SetCountLootFunctionAccessor)function).getAdd();
            if (add){
                stack.setCount(stack.getCount() + (int)rollAvg);
            } else {
                stack.setCount((int)rollAvg);
            }
            if (add){
                return Text.translatable("emi_loot.function.set_count_add");
            }
            return Text.empty();
        } else {
            return Text.empty();
        }
    }



    record ItemEntryResult(
            ItemStack item,
            int weight,
            List<Text> conditions,
            List<Text> functions
    ){}


}
