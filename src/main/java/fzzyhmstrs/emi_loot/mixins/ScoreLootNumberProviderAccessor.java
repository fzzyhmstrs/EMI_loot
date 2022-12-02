package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.provider.number.ScoreLootNumberProvider;
import net.minecraft.loot.provider.score.LootScoreProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScoreLootNumberProvider.class)
public interface ScoreLootNumberProviderAccessor {

    @Accessor(value = "target")
    LootScoreProvider getTarget();

    @Accessor(value = "score")
    String getScore();

    @Accessor(value = "scale")
    float getScale();
}
