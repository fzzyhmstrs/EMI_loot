package fzzyhmstrs.emi_loot.server;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public interface LootSender<T extends LootBuilder> {
    void send(ServerPlayerEntity player);
    void addBuilder(T builder);
    List<T> getBuilders();

    static String getIdToSend(Identifier id){
        if (id.getNamespace().equals("minecraft")){
            return id.getPath();
        }
        return id.toString();
    }
}
