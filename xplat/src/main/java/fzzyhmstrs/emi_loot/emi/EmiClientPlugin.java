package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.client.ClientArchaeologyLootTable;
import fzzyhmstrs.emi_loot.client.ClientBlockLootTable;
import fzzyhmstrs.emi_loot.client.ClientChestLootTable;
import fzzyhmstrs.emi_loot.client.ClientGameplayLootTable;
import fzzyhmstrs.emi_loot.client.ClientLootTables;
import fzzyhmstrs.emi_loot.client.ClientMobLootTable;
import fzzyhmstrs.emi_loot.client.LootReceiver;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@EmiEntrypoint
public class EmiClientPlugin implements EmiPlugin {
    private static final Identifier LOOT_ID = Identifier.of(EMILoot.MOD_ID, "chest_loot");
    private static final Identifier BLOCK_ID = Identifier.of(EMILoot.MOD_ID, "block_drops");
    private static final Identifier MOB_ID = Identifier.of(EMILoot.MOD_ID, "mob_drops");
    private static final Identifier GAMEPLAY_ID = Identifier.of(EMILoot.MOD_ID, "gameplay_drops");
    private static final Identifier ARCHAEOLOGY_ID = Identifier.of(EMILoot.MOD_ID, "archaeology_drops");
    public static final EmiRecipeCategory LOOT_CATEGORY = new EmiRecipeCategory(LOOT_ID, EmiStack.of(Blocks.CHEST.asItem()), new LootSimplifiedRenderer(0, 0));
    public static final EmiRecipeCategory BLOCK_CATEGORY = new EmiRecipeCategory(BLOCK_ID, EmiStack.of(Blocks.DIAMOND_ORE.asItem()), new LootSimplifiedRenderer(16, 0));
    public static final EmiRecipeCategory MOB_CATEGORY = new EmiRecipeCategory(MOB_ID, EmiStack.of(Blocks.ZOMBIE_HEAD.asItem()), new LootSimplifiedRenderer(0, 16));
    public static final EmiRecipeCategory GAMEPLAY_CATEGORY = new EmiRecipeCategory(GAMEPLAY_ID, EmiStack.of(Items.FISHING_ROD), new LootSimplifiedRenderer(16, 16));
    public static final EmiRecipeCategory ARCHAEOLOGY_CATEGORY = new EmiRecipeCategory(ARCHAEOLOGY_ID, EmiStack.of(Items.BRUSH));

    private static CompletableFuture<List<ChestLootRecipe>> chestFuture;
    private static CompletableFuture<List<BlockLootRecipe>> blockFuture;
    private static CompletableFuture<List<MobLootRecipe>> mobFuture;
    private static CompletableFuture<List<GameplayLootRecipe>> gameplayFuture;
    private static CompletableFuture<List<ArchaeologyLootRecipe>> archaeologyFuture;

    @Override
    public void initialize(EmiInitRegistry registry) {
        chestFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return ClientLootTables.INSTANCE.chestLoots.stream().flatMap(lr -> {
                    try {
                        return Stream.of(new ChestLootRecipe((ClientChestLootTable) lr));
                    } catch (Throwable e) {
                        EMILoot.LOGGER.error("Critical error encountered while creating a Chest loot recipe; recipe skipped: {}", lr.getId().toString());
                        EMILoot.LOGGER.error("Thrown Error: ", e);
                        return Stream.of();
                    }
                }).toList();
            } catch (Throwable e) {
                EMILoot.LOGGER.error("Critical error encountered while creating all Chest loot recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }
        });
        blockFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return ClientLootTables.INSTANCE.blockLoots.stream().flatMap(lr -> {
                    try {
                        return Stream.of(new BlockLootRecipe((ClientBlockLootTable) lr));
                    } catch (Throwable e) {
                        EMILoot.LOGGER.error("Critical error encountered while creating a Block loot recipe; recipe skipped: {}", lr.getId().toString());
                        EMILoot.LOGGER.error("Thrown Error: ", e);
                        return Stream.of();
                    }
                }).toList();
            } catch (Throwable e) {
                EMILoot.LOGGER.error("Critical error encountered while creating all Block Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }
        });
        mobFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return ClientLootTables.INSTANCE.mobLoots.stream().flatMap(lr -> {
                    try {
                        return Stream.of(new MobLootRecipe((ClientMobLootTable) lr));
                    } catch (Throwable e) {
                        EMILoot.LOGGER.error("Critical error encountered while creating a Mob loot recipe; recipe skipped: {}", lr.getId().toString());
                        EMILoot.LOGGER.error("Thrown Error: ", e);
                        return Stream.of();
                    }
                }).toList();
            } catch (Throwable e) {
                EMILoot.LOGGER.error("Critical error encountered while creating all Mob Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }
        });
        gameplayFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return ClientLootTables.INSTANCE.gameplayLoots.stream().flatMap(lr -> {
                    try {
                        return Stream.of(new GameplayLootRecipe((ClientGameplayLootTable) lr));
                    } catch (Throwable e) {
                        EMILoot.LOGGER.error("Critical error encountered while creating a Gameplay loot recipe; recipe skipped: {}", lr.getId().toString());
                        EMILoot.LOGGER.error("Thrown Error: ", e);
                        return Stream.of();
                    }
                }).toList();
            } catch (Throwable e) {
                EMILoot.LOGGER.error("Critical error encountered while creating all Gameplay Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }
        });
        archaeologyFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return ClientLootTables.INSTANCE.archaeologyLoots.stream().flatMap(lr -> {
                    try {
                        return Stream.of(new ArchaeologyLootRecipe((ClientArchaeologyLootTable) lr));
                    } catch (Throwable e) {
                        EMILoot.LOGGER.error("Critical error encountered while creating an Archaeology loot recipe; recipe skipped: {}", lr.getId().toString());
                        EMILoot.LOGGER.error("Thrown Error: ", e);
                        return Stream.of();
                    }
                }).toList();
            } catch (Throwable e) {
                EMILoot.LOGGER.error("Critical error encountered while creating all Archaeology Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }
        });
    }

    @Override
    public void register(EmiRegistry registry) {

        registry.addCategory(LOOT_CATEGORY);
        registry.addCategory(BLOCK_CATEGORY);
        registry.addCategory(MOB_CATEGORY);
        registry.addCategory(GAMEPLAY_CATEGORY);
        registry.addCategory(ARCHAEOLOGY_CATEGORY);

        if (chestFuture != null) {
            chestFuture.exceptionally(e -> {
                EMILoot.LOGGER.error("Critical error encountered while registering all Chest Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }).join().forEach(registry::addRecipe);
        } else {
            EMILoot.LOGGER.error("Chest loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Chest recipes skipped!");
        }
        if (blockFuture != null) {
            blockFuture.exceptionally(e -> {
                EMILoot.LOGGER.error("Critical error encountered while registering all Block Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }).join().forEach(registry::addRecipe);
        } else {
            EMILoot.LOGGER.error("Block loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Block recipes skipped!");
        }
        if (mobFuture != null) {
            mobFuture.exceptionally(e -> {
                EMILoot.LOGGER.error("Critical error encountered while registering all Mob Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }).join().forEach(registry::addRecipe);
        } else {
            EMILoot.LOGGER.error("Mob loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Mob recipes skipped!");
        }
        if (gameplayFuture != null) {
            gameplayFuture.exceptionally(e -> {
                EMILoot.LOGGER.error("Critical error encountered while registering all Gameplay Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }).join().forEach(registry::addRecipe);
        } else {
            EMILoot.LOGGER.error("Gameplay loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Gameplay recipes skipped!");
        }
        if (archaeologyFuture != null) {
            archaeologyFuture.exceptionally(e -> {
                EMILoot.LOGGER.error("Critical error encountered while registering all Archaeology Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }).join().forEach(registry::addRecipe);
        } else {
            EMILoot.LOGGER.error("Archaeology loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Archaeology recipes skipped!");
        }
    }
}