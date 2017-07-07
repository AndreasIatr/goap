package goap;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class CrossLakeTest {

    private static Action<String> chopWood;
    private static Action<String> buildRaft;
    private static LinkedList<ActionNode<String>> pathForGoalNoState;
    private static LinkedList<ActionNode<String>> pathForGoalWithState;
    private static int expectedTotalCost, expectedNumberOfNodes;
    private static String crossLake;

    @BeforeClass
    public static void setup() throws PathToGoalNotFoundException {
        String hasWood = "has wood";
        crossLake = "cross lake";
        String hasRaft = "has raft";

        expectedTotalCost = 0;
        expectedNumberOfNodes = 0;

        chopWood = new Action<>("Chop Wood", 1);
        chopWood.addEffect(hasWood);
        expectedTotalCost += chopWood.getCost();
        expectedNumberOfNodes++;

        Action<String> lookForWood = new Action<>("Look For Wood", () -> 2);
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

        Action<String> crossLakeBySwimming = new Action<>("Cross Lake By Swimming", () -> 12);
        crossLakeBySwimming.addEffect(crossLake);

        Action<String> goAroundTheLakeOnFoot = new Action<>("Go Around The Lake On Foot", 11);
        goAroundTheLakeOnFoot.addEffect(crossLake);

        Action<String> fail = new Action<>("Fail", () -> {
            fail();
            return 10;
        });
        fail.addPrecondition("Lazy Cost Calculation Fails");

        Planner<String> planner = new Planner<>();

        List<Action<String>> actions = asList(
                fail,
                chopWood, lookForWood, buildRaft, crossLakeByBoat,
                crossLakeBySwimming, goAroundTheLakeOnFoot);

        List<String> state = new LinkedList<>();

        pathForGoalNoState = planner.getPlan(actions, crossLake, state);

        state.add(hasRaft);
        pathForGoalWithState = planner.getPlan(actions, crossLake, state);
    }

    @Test
    public void can_reach_goal_no_state_test() {
        assertTrue(pathForGoalNoState.peekLast().getAction().getEffects().contains(crossLake));
    }

    @Test
    public void can_reach_goal_with_state_test() {
        assertTrue(pathForGoalWithState.peekLast().getAction().getEffects().contains(crossLake));
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
