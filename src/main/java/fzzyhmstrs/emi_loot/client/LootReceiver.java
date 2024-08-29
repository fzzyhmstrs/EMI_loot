package fzzyhmstrs.emi_loot.client;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface LootReceiver {
    boolean isEmpty();
    Identifier getId();
    LootReceiver fromBuf(PacketByteBuf buf, World world);

    default ItemStack readItemStack(PacketByteBuf buf, World world) {
        NbtCompound nbt = buf.readNbt();
        if (nbt == null) return ItemStack.EMPTY;
        return ItemStack.fromNbtOrEmpty(world.getRegistryManager(), nbt);
    }
}