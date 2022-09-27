package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.item.map.MapIcon;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.tag.TagKey;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExplorationMapLootFunction.class)
public interface ExplorationMapLootFunctionAccessor {

    @Accessor(value = "decoration")
    MapIcon.Type getDecoration();

    @Accessor(value = "destination")
    TagKey<Structure> getDestination();

}
