package fzzyhmstrs.emi_loot.mixins;

import fzzyhmstrs.emi_loot.util.SlimeEntitySizeSetter;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin implements SlimeEntitySizeSetter {

    @Shadow
    protected abstract void setSize(int size, boolean heal);

    @Override
    public void setSlimeSize(int size, boolean heal) {
        setSize(size, heal);
    }
}
