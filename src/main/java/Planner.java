import java.util.*;
import java.util.stream.Collectors;

public class Planner {
    // TODO add state param
    public Set<ActionNode> getPathForGoal(Collection<Action> actions, Action goal) {

        List<String> state = new ArrayList<>();

        // The set of nodes already evaluated.
        Set<ActionNode> closedSet = new HashSet<>();

        // most efficient previous steps.
        Set<ActionNode> cameFrom = new HashSet<>();

        // The set of currently discovered nodes still to be evaluated.
        // Initially, only the start nodes are known.
        Set<ActionNode> openSet = actions.stream()
                .filter(a -> a.getPreconditions().isEmpty()) // TODO add state check here
                .map(a -> new ActionNode(a, a.getCost()))
                .collect(Collectors.toSet());

        while (!openSet.isEmpty()) {
            ActionNode current = getLowestCostActionNode(openSet);

            if (current.getAction().equals(goal)) {
                cameFrom.add(current);
                return cameFrom;
            }

            openSet.remove(current);
            closedSet.add(current);

            List<String> currentState = new ArrayList<>();
            currentState.addAll(state);
            currentState.addAll(current.getAction().getEffects());

            getNeighborActionNodes(current, actions, currentState).stream()
                    .filter(an -> !closedSet.contains(an))
                    .filter(an -> !openSet.contains(an))
                    .forEach(an -> {
                        openSet.add(an);
                        cameFrom.add(current);
                        state.addAll(current.getAction().getEffects());
                    });

        }

        return cameFrom;
    }

    private ActionNode getLowestCostActionNode(Set<ActionNode> openSet) {
        return openSet.stream().min(ActionNode::compareTo).get();
    }

    /**
     *
     * @param current current node
     * @param actions available actions
     * @param state current state
     * @return nodes that can be traversed using current state
     */
    private List<ActionNode> getNeighborActionNodes(ActionNode current, Collection<Action> actions, List<String> state) {
        return actions.stream()
                .filter(a -> state.containsAll(a.getPreconditions()))
                .filter(a -> !a.equals(current.getAction()))
                .map(a -> new ActionNode(a, current.getTotalCost() + a.getCost()))
                .collect(Collectors.toList());
    }

}
