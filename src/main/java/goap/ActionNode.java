package goap;

import lombok.Data;

@Data
public class ActionNode<T> implements Comparable<ActionNode> {
    private final Action<T> action;
    private ActionNode<T> previous;
    private final int totalCost;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionNode that = (ActionNode) o;

        return action.equals(that.action);
    }

    @Override
    public int hashCode() {
        return action.hashCode();
    }


    @Override
    public int compareTo(ActionNode o) {
        int compareTo = Integer.compare(totalCost, o.totalCost);
        if (compareTo == 0) {
            compareTo = Integer.compare(action.getEffects().size(), o.action.getEffects().size());
            if (compareTo == 0)
                return -Integer.compare(action.getPreconditions().size(), o.action.getPreconditions().size());
            return compareTo;
        }
        return compareTo;
    }
}