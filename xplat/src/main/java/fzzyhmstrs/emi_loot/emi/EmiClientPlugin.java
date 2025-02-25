package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.client.ClientArchaeologyLootTable;
import fzzyhmstrs.emi_loot.client.ClientBlockLootTable;
import fzzyhmstrs.emi_loot.client.ClientChestLootTable;
import fzzyhmstrs.emi_loot.client.ClientGameplayLootTable;
import fzzyhmstrs.emi_loot.client.ClientLootTables;
import fzzyhmstrs.emi_loot.client.ClientMobLootTable;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@EmiEntrypoint
public class EmiClientPlugin implements EmiPlugin {
    private static final Identifier LOOT_ID = new Identifier(EMILoot.MOD_ID, "chest_loot");
    private static final Identifier BLOCK_ID = new Identifier(EMILoot.MOD_ID, "block_drops");
    private static final Identifier MOB_ID = new Identifier(EMILoot.MOD_ID, "mob_drops");
    private static final Identifier GAMEPLAY_ID = new Identifier(EMILoot.MOD_ID, "gameplay_drops");
    private static final Identifier ARCHAEOLOGY_ID = new Identifier(EMILoot.MOD_ID, "archaeology_drops");
    public static final EmiRecipeCategory CHEST_CATEGORY = new EmiRecipeCategory(LOOT_ID, EmiStack.of(Blocks.CHEST.asItem()), new LootSimplifiedRenderer(0, 0));
    public static final EmiRecipeCategory BLOCK_CATEGORY = new EmiRecipeCategory(BLOCK_ID, EmiStack.of(Blocks.DIAMOND_ORE.asItem()), new LootSimplifiedRenderer(16, 0));
    public static final EmiRecipeCategory MOB_CATEGORY = new EmiRecipeCategory(MOB_ID, EmiStack.of(Blocks.ZOMBIE_HEAD.asItem()), new LootSimplifiedRenderer(0, 16));
    public static final EmiRecipeCategory GAMEPLAY_CATEGORY = new EmiRecipeCategory(GAMEPLAY_ID, EmiStack.of(Items.FISHING_ROD), new LootSimplifiedRenderer(16, 16));
    public static final EmiRecipeCategory ARCHAEOLOGY_CATEGORY = new EmiRecipeCategory(ARCHAEOLOGY_ID, EmiStack.of(Items.BRUSH));

    private static CompletableFuture<List<ChestLootRecipe.ChestLootRecipeData>> chestFuture;
    private static CompletableFuture<List<BlockLootRecipe>> blockFuture;
    private static CompletableFuture<List<MobLootRecipe.MobLootRecipeData>> mobFuture;
    private static CompletableFuture<List<GameplayLootRecipe.GameplayLootRecipeData>> gameplayFuture;
    private static CompletableFuture<List<ArchaeologyLootRecipe.ArchaeologyLootRecipeData>> archaeologyFuture;

    @Override
    public void initialize(EmiInitRegistry registry) {
        chestFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return ClientLootTables.INSTANCE.chestLoots.stream().flatMap(lr -> {
                    try {
                        return Stream.of(ChestLootRecipe.ChestLootRecipeData.of((ClientChestLootTable) lr));
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
                        return Stream.of(new BlockLootRecipe(BlockLootRecipe.BlockLootRecipeData.of((ClientBlockLootTable) lr)));
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
                        return Stream.of(MobLootRecipe.MobLootRecipeData.of((ClientMobLootTable) lr));
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
                        return Stream.of(GameplayLootRecipe.GameplayLootRecipeData.of((ClientGameplayLootTable) lr));
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
                        return Stream.of(ArchaeologyLootRecipe.ArchaeologyLootRecipeData.of((ClientArchaeologyLootTable) lr));
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

        registry.addCategory(CHEST_CATEGORY);
        registry.addCategory(BLOCK_CATEGORY);
        registry.addCategory(MOB_CATEGORY);
        registry.addCategory(GAMEPLAY_CATEGORY);
        registry.addCategory(ARCHAEOLOGY_CATEGORY);

        CompletableFuture<Void> chestRecipeFuture = chestFuture == null ? null : chestFuture.thenApplyAsync(l -> {
            long t = Util.getMeasuringTimeNano();
            List<ChestLootRecipe> l2 = l.stream().map(ChestLootRecipe::new).toList();
            EMILoot.LOGGER.info("Built {} chest loot recipes in {}ms", l2.size(), (Util.getMeasuringTimeNano() - t)/1000000);
            return l2;
        }).exceptionally(e -> {
            EMILoot.LOGGER.error("Critical error encountered while registering all Chest Recipes. Recipes completely skipped!");
            EMILoot.LOGGER.error("Thrown Error: ", e);
            return null;
        }).thenAcceptAsync(l -> l.forEach(r -> registerRecipe(registry, r)));

        CompletableFuture<Void> blockRecipeFuture = blockFuture == null ? null : blockFuture.thenAcceptAsync(l -> {
            long t = Util.getMeasuringTimeNano();
            l.forEach(r -> {
                r.init();
                registerRecipe(registry, r);
            });
            EMILoot.LOGGER.info("Built {} block loot recipes in {}ms", l.size(), (Util.getMeasuringTimeNano() - t)/1000000);
        }).exceptionally(e -> {
            EMILoot.LOGGER.error("Critical error encountered while registering all Block Recipes. Recipes completely skipped!");
            EMILoot.LOGGER.error("Thrown Error: ", e);
            return null;
        });

        CompletableFuture<Void> mobRecipeFuture = mobFuture == null ? null : mobFuture.thenApplyAsync(l -> {
            long t = Util.getMeasuringTimeNano();
            List<MobLootRecipe> l2 = l.stream().map(MobLootRecipe::new).toList();
            EMILoot.LOGGER.info("Built {} mob loot recipes in {}ms", l2.size(), (Util.getMeasuringTimeNano() - t)/1000000);
            return l2;
        }).exceptionally(e -> {
            EMILoot.LOGGER.error("Critical error encountered while registering all Mob Recipes. Recipes completely skipped!");
            EMILoot.LOGGER.error("Thrown Error: ", e);
            return null;
        }).thenAcceptAsync(l -> l.forEach(r -> registerRecipe(registry, r)));

        CompletableFuture<Void> gameplayRecipeFuture = gameplayFuture == null ? null : gameplayFuture.thenApplyAsync(l -> {
            long t = Util.getMeasuringTimeNano();
            List<GameplayLootRecipe> l2 = l.stream().map(GameplayLootRecipe::new).toList();
            EMILoot.LOGGER.info("Built {} gameplay loot recipes in {}ms", l2.size(), (Util.getMeasuringTimeNano() - t)/1000000);
            return l2;
        }).exceptionally(e -> {
            EMILoot.LOGGER.error("Critical error encountered while registering all Gameplay Recipes. Recipes completely skipped!");
            EMILoot.LOGGER.error("Thrown Error: ", e);
            return null;
        }).thenAcceptAsync(l -> l.forEach(r -> registerRecipe(registry, r)));

        CompletableFuture<Void> archaeologyRecipeFuture = archaeologyFuture == null ? null : archaeologyFuture.thenApplyAsync(l -> {
            long t = Util.getMeasuringTimeNano();
            List<ArchaeologyLootRecipe> l2 = l.stream().map(ArchaeologyLootRecipe::new).toList();
            EMILoot.LOGGER.info("Built {} archaeology loot recipes in {}ms", l2.size(), (Util.getMeasuringTimeNano() - t)/1000000);
            return l2;
        }).exceptionally(e -> {
            EMILoot.LOGGER.error("Critical error encountered while registering all Archaeology Recipes. Recipes completely skipped!");
            EMILoot.LOGGER.error("Thrown Error: ", e);
            return null;
        }).thenAcceptAsync(l -> l.forEach(r -> registerRecipe(registry, r)));



        /*CompletableFuture<List<ChestLootRecipe>> chestRecipeFuture = chestFuture == null ? null : chestFuture.thenApplyAsync(l -> {
            long t = Util.getMeasuringTimeNano();
            List<ChestLootRecipe> l2 = l.stream().map(ChestLootRecipe::new).toList();
            EMILoot.LOGGER.info("Built {} chest loot recipes in {}ms", l2.size(), (Util.getMeasuringTimeNano() - t)/1000000);
            return l2;
        });
        CompletableFuture<List<BlockLootRecipe>> blockRecipeFuture = blockFuture == null ? null : blockFuture.thenApplyAsync(l -> {
            long t = Util.getMeasuringTimeNano();
            List<BlockLootRecipe> l2 = l.stream().map(BlockLootRecipe::new).toList();
            EMILoot.LOGGER.info("Built {} block loot recipes in {}ms", l2.size(), (Util.getMeasuringTimeNano() - t)/1000000);
            return l2;
        });
        CompletableFuture<List<MobLootRecipe>> mobRecipeFuture = mobFuture == null ? null : mobFuture.thenApplyAsync(l -> {
            long t = Util.getMeasuringTimeNano();
            List<MobLootRecipe> l2 = l.stream().map(MobLootRecipe::new).toList();
            EMILoot.LOGGER.info("Built {} mob loot recipes in {}ms", l2.size(), (Util.getMeasuringTimeNano() - t)/1000000);
            return l2;
        });
        CompletableFuture<List<GameplayLootRecipe>> gameplayRecipeFuture = gameplayFuture == null ? null : gameplayFuture.thenApplyAsync(l -> {
            long t = Util.getMeasuringTimeNano();
            List<GameplayLootRecipe> l2 = l.stream().map(GameplayLootRecipe::new).toList();
            EMILoot.LOGGER.info("Built {} gameplay loot recipes in {}ms", l2.size(), (Util.getMeasuringTimeNano() - t)/1000000);
            return l2;
        });
        CompletableFuture<List<ArchaeologyLootRecipe>> archaeologyRecipeFuture = archaeologyFuture == null ? null : archaeologyFuture.thenApplyAsync(l -> {
            long t = Util.getMeasuringTimeNano();
            List<ArchaeologyLootRecipe> l2 = l.stream().map(ArchaeologyLootRecipe::new).toList();
            EMILoot.LOGGER.info("Built {} archaeology loot recipes in {}ms", l2.size(), (Util.getMeasuringTimeNano() - t)/1000000);
            return l2;
        });*/

        long t = Util.getMeasuringTimeNano();
        if (gameplayRecipeFuture != null) {
            gameplayRecipeFuture.join();
        } else {
            EMILoot.LOGGER.error("Gameplay loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Gameplay recipes skipped!");
        }
        EMILoot.LOGGER.info("Registered gameplay loot recipes after {}ms", (Util.getMeasuringTimeNano() - t)/1000000);
        if (archaeologyRecipeFuture != null) {
            archaeologyRecipeFuture.join();
        } else {
            EMILoot.LOGGER.error("Archaeology loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Archaeology recipes skipped!");
        }
        EMILoot.LOGGER.info("Registered archaeology loot recipes after {}ms", (Util.getMeasuringTimeNano() - t)/1000000);
        if (mobRecipeFuture != null) {
            mobRecipeFuture.join();
        } else {
            EMILoot.LOGGER.error("Mob loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Mob recipes skipped!");
        }
        EMILoot.LOGGER.info("Registered mob loot recipes after {}ms", (Util.getMeasuringTimeNano() - t)/1000000);
        if (chestRecipeFuture != null) {
            chestRecipeFuture.join();
        } else {
            EMILoot.LOGGER.error("Chest loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Chest recipes skipped!");
        }
        EMILoot.LOGGER.info("Registered chest loot recipes after {}ms", (Util.getMeasuringTimeNano() - t)/1000000);
        if (blockRecipeFuture != null) {
            blockRecipeFuture.join();
        } else {
            EMILoot.LOGGER.error("Block loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Block recipes skipped!");
        }
        EMILoot.LOGGER.info("Registered all loot recipes after {}ms", (Util.getMeasuringTimeNano() - t)/1000000);

        /*long t = Util.getMeasuringTimeNano();
        if (chestRecipeFuture != null) {
            chestRecipeFuture.exceptionally(e -> {
                EMILoot.LOGGER.error("Critical error encountered while registering all Chest Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }).join().forEach(registry::addRecipe);
        } else {
            EMILoot.LOGGER.error("Chest loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Chest recipes skipped!");
        }
        EMILoot.LOGGER.info("Registered chest loot recipes after {}ms", (Util.getMeasuringTimeNano() - t)/1000000);
        if (blockRecipeFuture != null) {
            blockRecipeFuture.exceptionally(e -> {
                EMILoot.LOGGER.error("Critical error encountered while registering all Block Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }).join().forEach(registry::addRecipe);
        } else {
            EMILoot.LOGGER.error("Block loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Block recipes skipped!");
        }
        EMILoot.LOGGER.info("Registered block loot recipes after {}ms", (Util.getMeasuringTimeNano() - t)/1000000);
        if (mobRecipeFuture != null) {
            mobRecipeFuture.exceptionally(e -> {
                EMILoot.LOGGER.error("Critical error encountered while registering all Mob Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }).join().forEach(registry::addRecipe);
        } else {
            EMILoot.LOGGER.error("Mob loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Mob recipes skipped!");
        }
        EMILoot.LOGGER.info("Registered mob loot recipes after {}ms", (Util.getMeasuringTimeNano() - t)/1000000);
        if (gameplayRecipeFuture != null) {
            gameplayRecipeFuture.exceptionally(e -> {
                EMILoot.LOGGER.error("Critical error encountered while registering all Gameplay Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }).join().forEach(registry::addRecipe);
        } else {
            EMILoot.LOGGER.error("Gameplay loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Gameplay recipes skipped!");
        }
        EMILoot.LOGGER.info("Registered gameplay loot recipes after {}ms", (Util.getMeasuringTimeNano() - t)/1000000);
        if (archaeologyRecipeFuture != null) {
            archaeologyRecipeFuture.exceptionally(e -> {
                EMILoot.LOGGER.error("Critical error encountered while registering all Archaeology Recipes. Recipes completely skipped!");
                EMILoot.LOGGER.error("Thrown Error: ", e);
                return new ArrayList<>();
            }).join().forEach(registry::addRecipe);
        } else {
            EMILoot.LOGGER.error("Archaeology loot recipe future null for an unknown reason; there may have been a problem during EMI initialization");
            EMILoot.LOGGER.error("Archaeology recipes skipped!");
        }
        EMILoot.LOGGER.info("Registered all loot recipes after {}ms", (Util.getMeasuringTimeNano() - t)/1000000);*/


    }

    private synchronized void registerRecipe(EmiRegistry registry, EmiRecipe recipe) {
        registry.addRecipe(recipe);
    }
}