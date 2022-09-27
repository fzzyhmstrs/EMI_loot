package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.item.Instrument;
import net.minecraft.loot.function.SetGoatHornSoundLootFunction;
import net.minecraft.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetGoatHornSoundLootFunction.class)
public interface SetGoatHornLootFunctionAccessor {

    @Accessor(value = "field_39184")
    TagKey<Instrument> getInstrumentTag();

}
