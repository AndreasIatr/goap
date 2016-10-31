import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class CrossLakeTest {

    private static Action<String> reachDestination;
    private static Action<String> chopWood;
    private static Action<String> buildRaft;
    private static LinkedList<ActionNode<String>> pathForGoalNoState;
    private static LinkedList<ActionNode<String>> pathForGoalWithState;
    private static int expectedTotalCost, expectedNumberOfNodes;

    @BeforeClass
    public static void setup() {
        String hasWood = "has wood";
        String crossLake = "cross lake";
        String hasRaft = "has raft";

        expectedTotalCost = 0;
        expectedNumberOfNodes = 0;

        chopWood = new Action<>("Chop Wood", 1);
        chopWood.addEffect(hasWood);
        expectedTotalCost += chopWood.getCost();
        expectedNumberOfNodes++;

        Action<String> lookForWood = new Action<>("Look For Wood", 2);
        lookForWood.addEffect(hasWood);

        buildRaft = new Action<>("Build Raft", 5);
        buildRaft.addPrecondition(hasWood);
        buildRaft.addEffect(hasRaft);
        expectedTotalCost += buildRaft.getCost();
        expectedNumberOfNodes++;

        Action<String> crossLakeByBoat = new Action<>("Cross Lake By Boat", 3);
        crossLakeByBoat.addPrecondition(hasRaft);
        crossLakeByBoat.addEffect(crossLake);
        expectedTotalCost += crossLakeByBoat.getCost();
        expectedNumberOfNodes++;

        Action<String> crossLakeBySwimming = new Action<>("Cross Lake By Swimming", 12);
        crossLakeBySwimming.addEffect(crossLake);

        Action<String> goAroundTheLakeOnFoot = new Action<>("Go Around The Lake On Foot", 11);
        goAroundTheLakeOnFoot.addEffect(crossLake);

        reachDestination = new Action<>("Reach Destination", 1);
        reachDestination.addPrecondition(crossLake);
        expectedTotalCost += reachDestination.getCost();
        expectedNumberOfNodes++;

        Planner<String> planner = new Planner<>();

        List<Action<String>> actions = asList(
                chopWood, lookForWood, buildRaft, crossLakeByBoat,
                crossLakeBySwimming, goAroundTheLakeOnFoot, reachDestination);

        List<String> state = new LinkedList<>();

        pathForGoalNoState = planner.getPathForGoal(actions, reachDestination, state);

        state.add(hasRaft);
        pathForGoalWithState = planner.getPathForGoal(actions, reachDestination, state);
    }

    @Test
    public void can_reach_goal_no_state_test() {
        assertFalse("Could not find path", pathForGoalNoState.isEmpty());
        assertEquals(reachDestination, pathForGoalNoState.peekLast().getAction());
    }

    @Test
    public void can_reach_goal_with_state_test() {
        assertFalse("Could not find path", pathForGoalWithState.isEmpty());
        assertEquals(reachDestination, pathForGoalWithState.peekLast().getAction());
    }

    @Test
    public void number_of_nodes_no_state_test() {
        assertEquals(expectedNumberOfNodes, pathForGoalNoState.size());
    }

    @Test
    public void number_of_nodes_with_state_test() {
        assertEquals(expectedNumberOfNodes - 2, pathForGoalWithState.size());
    }

    @Test
    public void fastest_path_no_state_test() {
        assertEquals(expectedTotalCost, pathForGoalNoState.peekLast().getTotalCost());
    }

    @Test
    public void fastest_path_with_state_test() {
        assertEquals(expectedTotalCost - chopWood.getCost() -buildRaft.getCost(),
                pathForGoalWithState.peekLast().getTotalCost());
    }
}
