package fzzyhmstrs.emi_loot.mixins;

import fzzyhmstrs.emi_loot.server.ServerResourceData;
import fzzyhmstrs.emi_loot.util.LootManagerConditionManager;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = LootManager.class, priority = 10000)
public class LootManagerMixin implements LootManagerConditionManager {

    @Shadow
    private Map<LootDataKey<?>, ?> keyToValue;


    @Inject(method = "reload(Lnet/minecraft/resource/ResourceReloader$Synchronizer;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;Lnet/minecraft/util/profiler/Profiler;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private void emi_loot_loadDirectTables(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ServerResourceData.loadDirectTables(manager);
    }

    /*@Inject(method = "validate(Ljava/util/Map;)V", at = @At("RETURN"))
    private void emi_loot_lootTablesAfterFabric(Map<LootDataType<?>, Map<Identifier, ?>> lootData, CallbackInfo ci){
        LootTableParser.parseLootTables((LootManager)(Object)this, keyToValue);
    }*/

    @Override
    public Map<LootDataKey<?>, ?> getKeysToValues() {
        return keyToValue;
    }
}
