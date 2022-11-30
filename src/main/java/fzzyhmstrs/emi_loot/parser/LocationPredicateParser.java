package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.mixins.LightPredicateAccessor;
import fzzyhmstrs.emi_loot.mixins.LocationPredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

import java.util.Objects;

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
            NumberRange.IntRange range = ((LightPredicateAccessor) light).getRange();
            if (range.equals(NumberRange.IntRange.ANY)) {
                return LText.translatable("emi_loot.location_predicate.light_any");
            }
            Integer min = range.getMin();
            Integer max = range.getMax();
            if (Objects.equals(min, max) && min != null) {
                return LText.translatable("emi_loot.location_predicate.light", min);
            } else {
                return LText.translatable("emi_loot.location_predicate.light_2", min, max);
            }
        }

        return LText.empty();
    }

}
