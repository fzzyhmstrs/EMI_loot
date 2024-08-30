package fzzyhmstrs.emi_loot.mixins;

import com.google.gson.JsonElement;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ReloadableRegistries.class, priority = 10000)
public class ReloadableRegistriesMixin {

	@SuppressWarnings("unchecked")
	@Inject(method = "method_58279", at = @At("RETURN"))
	private static <T> void onLootTablesLoaded(LootDataType<T> lootDataType, ResourceManager resourceManager, RegistryOps<JsonElement> registryOps, CallbackInfoReturnable<MutableRegistry<?>> cir) {
		if (lootDataType != LootDataType.LOOT_TABLES) return;
		EMILoot.parseTables(resourceManager, (Registry<LootTable>) cir.getReturnValue(), registryOps);
	}
}