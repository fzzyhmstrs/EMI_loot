package fzzyhmstrs.emi_loot.util;

import com.google.common.base.Suppliers;
import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class WidgetRowBuilder {

    public WidgetRowBuilder(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    private final int maxWidth;
    private final List<ClientBuiltPool> poolList = new LinkedList<>();
    private final Supplier<List<ConditionalStack>> stacks = Suppliers.memoize(() -> {
        List<ConditionalStack> list = new ArrayList<>();
        for (ClientBuiltPool pool: poolList) {
            list.addAll(
                    pool.stacks().stream().map(
                            stack -> stack.ingredient().stream().map(
                                    s -> new ConditionalStack(stack.conditions(), stack.weight(), List.of(s))
                            ).toList()
                    ).collect(
                            ArrayList::new, ArrayList::addAll, ArrayList::addAll
                    )
            );
        }
        return list;
    });
    private int width = 0;

    public List<ClientBuiltPool> getPoolList() {
        return poolList;
    }

    public int ingredientCount() {
        int count = 0;
        for (ClientBuiltPool pool: poolList) {
            count += pool.stacks().size();
        }
        return count;
    }

    public List<ConditionalStack> ingredients() {
        List<ConditionalStack> list = new ArrayList<>();
        for (ClientBuiltPool pool: poolList) {
            list.addAll(pool.stacks());
        }
        return list;
    }

    public List<ConditionalStack> stacks() {
        return stacks.get();
    }

    public int getWidth() {
        return width;
    }

    private boolean add(ClientBuiltPool newPool) {
        int newWidth = getNewWidth(newPool);
        //System.out.println(newPool.stackMap());
        //System.out.println("width: " + (width + newWidth));
        if (width + newWidth > maxWidth) return false;
        width += newWidth;
        poolList.add(newPool);
        return true;
    }

    public Optional<ClientBuiltPool> addAndTrim(ClientBuiltPool newPool) {
        if (add(newPool)) return Optional.empty();
        if (width == 0) {
            List<ConditionalStack> madeItIn = new ArrayList<>();
            List<ConditionalStack> leftOvers = new ArrayList<>();
            AtomicInteger newWidth = new AtomicInteger(14 + (11 * (((newPool.conditions().size() - 1) / 2) - 1)));
            newPool.stacks().forEach(s -> {
                float weight = s.weight();
                List<EmiStack> stacks = s.ingredient();
                if (newWidth.addAndGet(20) <= maxWidth) {
                    madeItIn.add(new ConditionalStack(s.conditions(), weight, stacks));
                } else {
                    leftOvers.add(new ConditionalStack(s.conditions(), weight, stacks));
                }
            });
            add(new ClientBuiltPool(newPool.conditions(), madeItIn));
            return Optional.of(new ClientBuiltPool(newPool.conditions(), leftOvers));
        } else {
            return Optional.of(newPool);
        }
    }

    private int getNewWidth(ClientBuiltPool newPool) {
        int newWidth;
        if (poolList.isEmpty()) {
            newWidth = 14 + (11 * ((newPool.conditions().size() - 1)/2)) + 20 * newPool.stacks().size();
        } else {
            newWidth = 20 + (11 * ((newPool.conditions().size() - 1)/2)) + 20 * newPool.stacks().size();
        }
        return newWidth;
    }

    public boolean canAddPool(ClientBuiltPool newPool) {
        int newWidth = getNewWidth(newPool);
        return width + newWidth <= maxWidth;
    }

    public boolean canAddOther(WidgetRowBuilder other) {
        return (width + other.width + 6) <= maxWidth;
    }

    public void addOther(WidgetRowBuilder other) {
        this.poolList.addAll(other.poolList);
    }

    public static void collate(List<WidgetRowBuilder> builders) {
        if (builders.isEmpty()) return;
        Int2IntMap collateMap = new Int2IntOpenHashMap();
        for (int i = 0; i < builders.size(); i++) {
            for (int j = 0; j < builders.size(); j++) {
                if (i==j) continue;
                if (builders.get(j).canAddOther(builders.get(i))) {
                    collateMap.put(j, i);
                }
            }
        }
        if (!collateMap.isEmpty()) {
            Int2IntMap.Entry start = collateMap.int2IntEntrySet().stream().toList().get(0);
        }
    }

}