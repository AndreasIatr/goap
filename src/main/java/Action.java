import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class Action {
    private final String name;

    private final int cost;

    private List<String> preconditions;
    public void addPrecondition(String name) {
        preconditions.add(name);
    }

    private List<String> effects;
    public void addEffect(String name) {
        effects.add(name);
    }

    public Action(String name, int cost) {
        this.name = name;
        this.cost = cost;
        preconditions = new LinkedList<>();
        effects = new LinkedList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        return cost == action.cost && name.equals(action.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + cost;
        return result;
    }
}
