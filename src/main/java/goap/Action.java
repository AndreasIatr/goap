package goap;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Action<T> {
    @Getter
    private final String name;

    private final Supplier<Integer> costSupplier;
    private Integer cost;
    public int getCost() {
        if (cost == null) {
            cost = costSupplier.get();
        }
        return cost;
    }

    private List<T> preconditions = new LinkedList<>();
    public List<T> getPreconditions() {
        if (preconditions.isEmpty()) {
            preconditions = getCalculatedPreconditions();
        }
        return preconditions;
    }

    private final List<Supplier<T>> lazyPreconditions = new LinkedList<>();
    private List<T> getCalculatedPreconditions() {
        return lazyPreconditions.stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
    }
    public void addPrecondition(Supplier<T> precondition) {
        lazyPreconditions.add(precondition);
    }
    public void addPrecondition(T precondition) {
        lazyPreconditions.add(() -> precondition);
    }

    private List<T> effects = new LinkedList<>();
    public List<T> getEffects() {
        if (effects.isEmpty()) {
            effects = getCalculatedEffects();
        }
        return effects;
    }

    private final List<Supplier<T>> lazyEffects = new LinkedList<>();
    private List<T> getCalculatedEffects() {
        return lazyEffects.stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
    }
    public void addEffect(Supplier<T> effect) {
        lazyEffects.add(effect);
    }
    public void addEffect(T effect) {
        lazyEffects.add(() -> effect);
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
