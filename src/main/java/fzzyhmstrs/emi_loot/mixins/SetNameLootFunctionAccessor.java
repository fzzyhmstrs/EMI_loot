package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(SetNameLootFunction.class)
public interface SetNameLootFunctionAccessor {

    @Accessor(value = "name")
    Optional<Text> getName();

}
