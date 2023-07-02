package fzzyhmstrs.emi_loot.mixins;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LootManagerConditionManager;
import net.minecraft.loot.LootManager;
import net.minecraft.resource.ProfiledResourceReload;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ProfiledResourceReload.class)
public class ProfiledResourceReloadMixin {

    @Inject(method = "method_18355", at = @At("RETURN"), cancellable = true)
    private static void emi_loot_readTablesAfterFabricForRealProfiled(Executor executor, ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, ResourceReloader reloader, Executor prepare, Executor apply, CallbackInfoReturnable<CompletableFuture> cir){
        if (reloader instanceof LootManager){
            cir.setReturnValue(cir.getReturnValue().thenRun(() -> LootTableParser.parseLootTables((LootManager) reloader,((LootManagerConditionManager)reloader).getKeysToValues())));
        }
    }


}
