package goap;

import lombok.Data;
import lombok.Setter;

import java.util.Comparator;

@Data
public class ActionNode<T> implements Comparable<ActionNode> {
    private final Action<T> action;
    private ActionNode<T> previous;
    private final int totalCost;
    @Setter
    private Comparator<ActionNode<T>> comparator = Comparator.comparing(ActionNode::getTotalCost);

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
        return comparator.compare(this, o);
    }
}
