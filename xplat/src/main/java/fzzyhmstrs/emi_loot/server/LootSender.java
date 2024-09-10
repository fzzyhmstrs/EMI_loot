package fzzyhmstrs.emi_loot.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public interface LootSender<T extends LootBuilder> {
    String getId();
    void send(ServerPlayerEntity player);
    void addBuilder(T builder);
    List<T> getBuilders();
    void build();

    static String getIdToSend(Identifier id) {
        if (id.getNamespace().equals("minecraft")) {
            String path = id.getPath();
            if (path.contains("blocks/")) {
                return "b/" + path.substring(7);
            } else if (path.contains("entities/")) {
                return "e/"+ path.substring(9);
            } else if (path.contains("chests/")) {
                return "c/" + path.substring(7);
            } else if (path.contains("gameplay/")) {
                return "g/" + path.substring(9);
            } else if (path.contains("archaeology/")) {
                return "a/" + path.substring(12);
            } else {
                return path;
            }
        }
        return id.toString();
    }

    default void writeItemStack(PacketByteBuf buf, ItemStack stack, World world) {
        NbtElement nbt = stack.encode(world.getRegistryManager());
        buf.writeNbt(nbt);
    }
}