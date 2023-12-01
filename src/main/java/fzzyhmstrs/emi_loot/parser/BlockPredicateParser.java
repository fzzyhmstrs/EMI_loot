package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.block.Block;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class BlockPredicateParser {

    public static Text parseBlockPredicate(BlockPredicate predicate){
        return LText.translatable("emi_loot.block_predicate.base", parseBlockPredicateInternal(predicate).getString());
    }

    private static Text parseBlockPredicateInternal(BlockPredicate predicate){
        Optional<TagKey<Block>> tag = predicate.tag();
        if (tag.isPresent()){
            return LText.translatable("emi_loot.block_predicate.tag",tag.get().id().toString());
        }

        Optional<RegistryEntryList<Block>> blocks = predicate.blocks();
        if (blocks.isPresent() && blocks.get().size() > 0){
            List<MutableText> list = blocks.get().stream().map(entry -> entry.value().getName()).toList();
            return LText.translatable("emi_loot.block_predicate.list_1", ListProcessors.buildOrList(list));
        }

        Optional<StatePredicate> statePredicate = predicate.state();
        if (statePredicate.isPresent()){
            return StatePredicateParser.parseStatePredicate(statePredicate.get());
        }

        Optional<NbtPredicate> nbt = predicate.nbt();
        if (nbt.isPresent()){
            return NbtPredicateParser.parseNbtPredicate(nbt.get());
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable block predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }
}
