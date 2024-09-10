package fzzyhmstrs.emi_loot.util;

import me.fzzyhmstrs.fzzy_config.networking.FzzyPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record SimpleFzzyPayload(PacketByteBuf buf, Identifier id) implements FzzyPayload {
    @Override
    public void write(@NotNull PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBytes(buf);
    }

    @Override
    public @NotNull Identifier getId() {
        return id;
    }
}
