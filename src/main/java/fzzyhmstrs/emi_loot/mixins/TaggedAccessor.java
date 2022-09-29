package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(targets = "net/minecraft/predicate/entity/EntityTypePredicate$Tagged")
public interface TaggedAccessor {

    @Accessor(value = "tag")
    TagKey<EntityType<?>> getTag();

}
