package fzzyhmstrs.emi_loot.server;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public interface LootSender<T extends LootBuilder> {
    String getId();
    void send(ServerPlayerEntity player);
    void addBuilder(T builder);
    List<T> getBuilders();
    void build();

    static String getIdToSend(Identifier id){
        if (id.getNamespace().equals("minecraft")){
            String path = id.getPath();
            return path.contains("blocks/") ? id.getPath().substring(7) : path; //substring after "blocks/"
        }
        return id.toString();
    }
}
