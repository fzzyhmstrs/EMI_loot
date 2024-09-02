package fzzyhmstrs.emi_loot.mixins;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.PlayerPredicate;
import net.minecraft.stat.Stat;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PlayerPredicate.class)
public interface PlayerPredicateAccessor {

    @Accessor(value = "experienceLevel")
    NumberRange.IntRange getExperienceLevel();

    @Accessor(value = "gameMode")
    GameMode getGameMode();

    @Accessor(value = "stats")
    Map<Stat<?>, NumberRange.IntRange> getStats();

    @Accessor(value = "recipes")
    Object2BooleanMap<Identifier> getRecipes();

    @Accessor(value = "advancements")
    Map<Identifier, PlayerPredicate.AdvancementPredicate> getAdvancements();

    @Accessor(value = "lookingAt")
    EntityPredicate getLookingAt();
}
