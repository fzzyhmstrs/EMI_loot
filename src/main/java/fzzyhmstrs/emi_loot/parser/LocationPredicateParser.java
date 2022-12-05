package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.LocationPredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

public class LocationPredicateParser {

    public static Text parseLocationPredicate(LocationPredicate predicate){
        NumberRange.FloatRange x = ((LocationPredicateAccessor)predicate).getX();
        if (!x.equals(NumberRange.FloatRange.ANY)){
            return LText.translatable("emi_loot.location_predicate.x",x.getMin(),x.getMax());
        }

        NumberRange.FloatRange y = ((LocationPredicateAccessor)predicate).getY();
        if (!y.equals(NumberRange.FloatRange.ANY)){
            return LText.translatable("emi_loot.location_predicate.y",y.getMin(),y.getMax());
        }

        NumberRange.FloatRange z = ((LocationPredicateAccessor)predicate).getZ();
        if (!z.equals(NumberRange.FloatRange.ANY)){
            return LText.translatable("emi_loot.location_predicate.z",z.getMin(),z.getMax());
        }

        RegistryKey<World> dim = ((LocationPredicateAccessor)predicate).getDimension();
        if (dim != null){
            return LText.translatable("emi_loot.location_predicate.dim",dim.getValue().toString());
        }

        RegistryKey<Biome> biome = ((LocationPredicateAccessor)predicate).getBiome();
        if (biome != null){
            return LText.translatable("emi_loot.location_predicate.biome",biome.getValue().toString());
        }

        RegistryKey<Structure> feature = ((LocationPredicateAccessor)predicate).getFeature();
        if (feature != null){
            return LText.translatable("emi_loot.location_predicate.structure",feature.getValue().toString());
        }

        Boolean smokey = ((LocationPredicateAccessor)predicate).getSmokey();
        if (smokey != null){
            if (smokey){
                return LText.translatable("emi_loot.location_predicate.smoke_true");
            } else {
                return LText.translatable("emi_loot.location_predicate.smoke_false");
            }
        }

        LightPredicate light = ((LocationPredicateAccessor)predicate).getLight();
        if (!light.equals(LightPredicate.ANY)) {
            return LightPredicateParser.parseLightPredicate(light);
        }

        BlockPredicate block = ((LocationPredicateAccessor)predicate).getBlock();
        if (!block.equals(BlockPredicate.ANY)) {
            return BlockPredicateParser.parseBlockPredicate(block);
        }

        FluidPredicate fluid = ((LocationPredicateAccessor)predicate).getFluid();
        if (!fluid.equals(FluidPredicate.ANY)){
            return FluidPredicateParser.parseFluidPredicate(fluid);
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable location predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}
