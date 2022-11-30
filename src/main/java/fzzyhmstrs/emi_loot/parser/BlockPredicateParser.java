package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.mixins.BlockPredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.block.Block;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Set;

public class BlockPredicateParser {

    public static Text parseBlockPredicate(BlockPredicate predicate){
        return LText.translatable("emi_loot.block_predicate.base", parseBlockPredicateInternal(predicate).getString());
    }

    private static Text parseBlockPredicateInternal(BlockPredicate predicate){
        TagKey<Block> tag = ((BlockPredicateAccessor)predicate).getTag();
        if (tag != null){
            return LText.translatable("emi_loot.block_predicate.tag",tag.id().toString());
        }

        Set<Block> blocks = ((BlockPredicateAccessor)predicate).getBlocks();
        if (blocks != null && !blocks.isEmpty()){
            List<Block> list = blocks.stream().toList();
            int size = list.size();
            if (size == 1){
                return LText.translatable("emi_loot.block_predicate.list_1",list.get(0));
            } else {
                return buildList(list,0);
            }
        }

        NbtPredicate nbt = ((BlockPredicateAccessor)predicate).getNbt();
        if (!nbt.equals(NbtPredicate.ANY)){
            return NbtPredicateParser.parseNbtPredicate(nbt);
        }

        return LText.literal("with a certain blockstate");
    }

    private static Text buildList(List<Block> blockList,int currentIndex){
        if (currentIndex == (blockList.size() - 1)){
            return blockList.get(currentIndex).getName();
        } else if (currentIndex == (blockList.size() - 2)){
            return LText.translatable("emi_loot.block_predicate.list_2",blockList.get(currentIndex).getName().getString(),buildList(blockList, currentIndex + 1).getString());
        } else {
            return LText.translatable("emi_loot.block_predicate.list_3",blockList.get(currentIndex).getName().getString(),buildList(blockList, currentIndex + 1).getString());
        }
    }

}
