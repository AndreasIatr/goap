package goap;

import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class Planner<T> {

    public LinkedList<ActionNode<T>> getPlan(
            @NonNull Set<Action<T>> actions,
            @NonNull T goal,
            @NonNull Collection<T> state) throws PathToGoalNotFoundException {

        if (actions.isEmpty()) {
            throw new PathToGoalNotFoundException(goal);
        }

        Set<Action<T>> localActions = new HashSet<>(actions);
        List<LinkedList<ActionNode<T>>> paths = new LinkedList<>();

        LinkedList<ActionNode<T>> pathForGoal = getPossiblePathsToGoal(localActions, goal, state);

        while (!(localActions.isEmpty() || pathForGoal.isEmpty())) {

            if (pathForGoal.peekLast().getAction().getEffects().contains(goal)) {
                paths.add(pathForGoal);
            }

            // force a different startNode on the next path
            localActions.remove(pathForGoal.peekFirst().getAction());
            pathForGoal = getPossiblePathsToGoal(localActions, goal, state);
        }

        if (paths.isEmpty()) {
            throw new PathToGoalNotFoundException(goal);
        }

        return getFastestOfPaths(paths);
    }

    private LinkedList<ActionNode<T>> getPossiblePathsToGoal(Collection<Action<T>> actions, T goal, Collection<T> state) {


        // The set of nodes already evaluated.
        Set<ActionNode<T>> closedSet = new HashSet<>();

        // The set of currently discovered nodes still to be evaluated.
        // Initially, only the start nodes are known.
        Set<ActionNode<T>> openSet = actions.stream()
                .filter(a -> state.containsAll(a.getPreconditions()))
                .map(a -> new ActionNode<>(a, a.getCost()))
                .collect(Collectors.toSet());

        while (!openSet.isEmpty()) {
            ActionNode<T> current = Collections.min(openSet);

            if (current.getAction().getEffects().contains(goal)) {
                return calcPath(current);
            }

            openSet.remove(current);
            closedSet.add(current);

            Set<T> currentState = new HashSet<>();
            currentState.addAll(state);
            currentState.addAll(current.getAction().getEffects());

            getNeighborActionNodes(current, actions, currentState).stream()
                    .filter(an -> !closedSet.contains(an))
                    .filter(an -> !openSet.contains(an))
                    .forEach(an -> {
                        openSet.add(an);

                        an.setPrevious(current);
                    });

        }

        return new LinkedList<>();
    }

    /**
     * Trace back using the goalNode to construct the path
     *
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
     * @param current current node
     * @param actions available actions
     * @param state   current state
     * @return nodes that can be traversed using current state
     */
    private List<ActionNode<T>> getNeighborActionNodes(ActionNode<T> current, Collection<Action<T>> actions, Collection<T> state) {
        return actions.stream()
                .filter(a -> state.containsAll(a.getPreconditions()))
                .filter(a -> !a.equals(current.getAction()))
                .map(a -> new ActionNode<>(a, current.getTotalCost() + a.getCost()))
                .collect(Collectors.toList());
    }

}
