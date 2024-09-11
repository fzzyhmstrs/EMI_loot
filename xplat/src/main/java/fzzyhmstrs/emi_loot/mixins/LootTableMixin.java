package fzzyhmstrs.emi_loot.mixins;

import fzzyhmstrs.emi_loot.util.LootTablePools;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootTable.class)
public class LootTableMixin implements LootTablePools {
    @Unique
    LootPool[] emi_loot$pools;

    @Override
    public LootPool[] getPools() {
        return this.emi_loot$pools;
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void emi_loot_initPools(LootContextType type, Identifier randomSequenceId, LootPool[] pools, LootFunction[] functions, CallbackInfo ci) {
        this.emi_loot$pools = pools;
    }
}
