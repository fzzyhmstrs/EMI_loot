package fzzyhmstrs.emi_loot.mixins;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataPackContents.class)
public class DataPackContentsMixin {

    @Inject(method = "refresh", at = @At("TAIL"))
    private void emi_loot_postProcessBuildersAfterTagReload(DynamicRegistryManager dynamicRegistryManager, CallbackInfo ci){
        LootTableParser.postProcess(LootTableParser.PostProcessor.TAG);
    }

}
