package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.entry.LeafEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LeafEntry.class)
public interface LeafEntryAccessor {

    @Accessor(value = "weight")
    int getWeight();
}
