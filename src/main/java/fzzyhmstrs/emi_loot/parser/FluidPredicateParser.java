package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.FluidPredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.fluid.Fluid;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public class FluidPredicateParser {

    public static Text parseFluidPredicate(FluidPredicate predicate){
        return LText.translatable("emi_loot.fluid_predicate.base",parseFluidPredicateInternal(predicate).getString());
    }

    private static Text parseFluidPredicateInternal(FluidPredicate predicate){

        TagKey<Fluid> tag = ((FluidPredicateAccessor)predicate).getTag();
        if (tag != null){
            return LText.translatable("emi_loot.fluid_predicate.tag",tag.id().toString());
        }

        Fluid fluid = ((FluidPredicateAccessor)predicate).getFluid();
        if (fluid != null){
            return LText.translatable("emi_loot.fluid_predicate.fluid", Registry.FLUID.getId(fluid).toString());
        }

        StatePredicate statePredicate = ((FluidPredicateAccessor)predicate).getState();
        if (!statePredicate.equals(StatePredicate.ANY)){
            return StatePredicateParser.parseStatePredicate(statePredicate);
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable fluid predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}
