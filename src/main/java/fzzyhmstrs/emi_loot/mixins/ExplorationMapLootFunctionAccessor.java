package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.item.map.MapDecorationType;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExplorationMapLootFunction.class)
public interface ExplorationMapLootFunctionAccessor {

    @Accessor(value = "decoration")
    RegistryEntry<MapDecorationType> getDecoration();

    @Accessor(value = "destination")
    TagKey<Structure> getDestination();

}