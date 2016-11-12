import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class Planner<T> {

    public LinkedList<ActionNode<T>> getPlan(@NonNull Collection<Action<T>> actions, @NonNull Action<T> goal, @NonNull List<T> state) {
        if (actions.isEmpty() || !actions.contains(goal)) {
            return new LinkedList<>();
        }

        LinkedList<Action<T>> localActions = new LinkedList<>(actions);
        List<T> localState = new ArrayList<>(state);
        List<LinkedList<ActionNode<T>>> paths = new LinkedList<>();

        LinkedList<ActionNode<T>> pathForGoal = getPossiblePathsToGoal(localActions, goal, localState);

        while (!(localActions.isEmpty() || pathForGoal.isEmpty())) {

            if (pathForGoal.peekLast().getAction().equals(goal)) {
                paths.add(pathForGoal);
            }

            // force a different startNode on the next path
            localActions.remove(pathForGoal.peekFirst().getAction());
            pathForGoal = getPossiblePathsToGoal(localActions, goal, localState);
        }

        return getFastestOfPaths(paths);
    }

    private LinkedList<ActionNode<T>> getPossiblePathsToGoal(Collection<Action<T>> actions, Action<T> goal, List<T> state) {

        List<T> localState = new ArrayList<>(state);

        // The set of nodes already evaluated.
        Set<ActionNode> closedSet = new HashSet<>();

        // The set of currently discovered nodes still to be evaluated.
        // Initially, only the start nodes are known.
        Set<ActionNode<T>> openSet = actions.stream()
                .filter(a -> localState.containsAll(a.getPreconditions()))
                .map(a -> new ActionNode<>(a, a.getCost()))
                .collect(Collectors.toSet());

        while (!openSet.isEmpty()) {
            ActionNode<T> current = Collections.min(openSet);

            if (current.getAction().equals(goal)) {
                return calcPath(current);
            }

            openSet.remove(current);
            closedSet.add(current);

            List<T> currentState = new LinkedList<>();
            currentState.addAll(localState);
            currentState.addAll(current.getAction().getEffects());

            getNeighborActionNodes(current, actions, currentState).stream()
                    .filter(an -> !closedSet.contains(an))
                    .filter(an -> !openSet.contains(an))
                    .forEach(an -> {
                        openSet.add(an);

                        an.setPrevious(current);
                        localState.addAll(current.getAction().getEffects());
                    });

        }

        return new LinkedList<>();
    }

    /**
     * Trace back using the goalNode to construct the path
     * @param goalNode the ActionNode representing the goal
     * @return a LinkedList that describes the path from start node to goal
     */
    private LinkedList<ActionNode<T>> calcPath(ActionNode<T> goalNode) {
        LinkedList<ActionNode<T>> path = new LinkedList<>();
        path.add(goalNode);
        ActionNode<T> current = goalNode.getPrevious();
        while (current != null) {
            path.push(current);
            current = current.getPrevious();
        }
        return path;
    }

    private LinkedList<ActionNode<T>> getFastestOfPaths(List<LinkedList<ActionNode<T>>> paths) {
        return Collections.min(paths, (o1, o2) -> {
            if (o1.isEmpty() && o2.isEmpty()) {
                return 0;
            } else if (o1.isEmpty()) {
                return -1;
            } else if (o2.isEmpty()) {
                return 1;
            } else {
                int compare = Integer.compare(o1.peekLast().getTotalCost(), o2.peekLast().getTotalCost());
                if (compare == 0) {
                    return Integer.compare(o1.size(), o2.size());
                }
                return compare;
            }
        });
    }

    /**
     *
     * @param current current node
     * @param actions available actions
     * @param state current state
     * @return nodes that can be traversed using current state
     */
    private List<ActionNode<T>> getNeighborActionNodes(ActionNode<T> current, Collection<Action<T>> actions, List<T> state) {
        return actions.stream()
                .filter(a -> state.containsAll(a.getPreconditions()))
                .filter(a -> !a.equals(current.getAction()))
                .map(a -> new ActionNode<>(a, current.getTotalCost() + a.getCost()))
                .collect(Collectors.toList());
    }

}
