package fzzyhmstrs.emi_loot.mixins;

import com.google.gson.JsonElement;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = LootManager.class, priority = 3000)
public class LootManagerMixin {
    @Shadow
    private Map<Identifier, LootTable> tables;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("RETURN"))
    private void lootTablesAfterFabric(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci){
        LootTableParser.parseLootTables((LootManager)(Object)this,tables);
    }

}
