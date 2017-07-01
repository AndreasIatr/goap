package goap;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@Data
public class Action<T> {
    private final String name;

    private final Supplier<Integer> costSupplier;
    private Integer cost;
    public int getCost() {
        if (cost == null) {
            cost = costSupplier.get();
        }
        return cost;
    }

    private final List<T> preconditions = new LinkedList<>();
    public void addPrecondition(T precondition) {
        preconditions.add(precondition);
    }

    private final List<T> effects = new LinkedList<>();
    public void addEffect(T effect) {
        effects.add(effect);
    }

    public Action(String name, int cost) {
        this.name = name;
        this.costSupplier = () -> cost;
    }

    public Action(String name, Supplier<Integer> costSupplier) {
        this.name = name;
        this.costSupplier = costSupplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        return name.equals(action.name);
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode();
    }
}
