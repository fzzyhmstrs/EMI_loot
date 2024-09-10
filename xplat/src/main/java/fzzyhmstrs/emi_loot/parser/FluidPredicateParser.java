package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.fluid.Fluid;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class FluidPredicateParser {

    public static Text parseFluidPredicate(FluidPredicate predicate) {
        return LText.translatable("emi_loot.fluid_predicate.base", parseFluidPredicateInternal(predicate).getString());
    }

    private static Text parseFluidPredicateInternal(FluidPredicate predicate) {
        Optional<RegistryEntryList<Fluid>> fluid = predicate.fluids();
        if (fluid.isPresent() && fluid.get().getTagKey().isPresent()) {
            return LText.translatable("emi_loot.fluid_predicate.tag", fluid.get().getTagKey().get().id().toString());
        }


        if (fluid.isPresent() && fluid.get().size() > 0) {
            List<MutableText> list = fluid.get().stream().map(entry -> entry.value().getDefaultState().getBlockState().getBlock().getName()).toList();
            return LText.translatable("emi_loot.fluid_predicate.fluid", ListProcessors.buildOrList(list));
        }

        Optional<StatePredicate> statePredicate = predicate.state();
        if (statePredicate.isPresent()) {
            return StatePredicateParser.parseStatePredicate(statePredicate.get());
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable fluid predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}