package fzzyhmstrs.emi_loot.mixins;

import com.google.gson.JsonElement;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.server.ServerResourceData;
import fzzyhmstrs.emi_loot.util.LootManagerConditionManager;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = LootManager.class, priority = 10000)
public class LootManagerMixin implements LootManagerConditionManager {

    @Final
    @Shadow
    private LootConditionManager conditionManager;

    @Override
    public LootConditionManager getManager() {
        return conditionManager;
    }

    @Shadow
    private Map<Identifier, LootTable> tables;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("RETURN"))
    private void emi_loot_lootTablesAfterFabric(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci){
        ServerResourceData.loadDirectTables(resourceManager);
        LootTableParser.parseLootTables((LootManager)(Object)this,tables);
    }
}
