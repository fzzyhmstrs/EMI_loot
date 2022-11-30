package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LocationPredicate.class)
public interface LocationPredicateAccessor {

    @Accessor(value = "x")
    NumberRange.FloatRange getX();

    @Accessor(value = "y")
    NumberRange.FloatRange getY();

    @Accessor(value = "z")
    NumberRange.FloatRange getZ();

    @Accessor(value = "biome")
    RegistryKey<Biome> getBiome();

    @Accessor(value = "feature")
    RegistryKey<Structure> getFeature();

    @Accessor(value = "dimension")
    RegistryKey<World> getDimension();

    @Accessor(value = "smokey")
    Boolean getSmokey();

    @Accessor(value = "light")
    LightPredicate getLight();

    @Accessor(value = "block")
    BlockPredicate getBlock();

    @Accessor(value = "fluid")
    FluidPredicate getFluid();
}
