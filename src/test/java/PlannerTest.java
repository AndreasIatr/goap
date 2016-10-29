import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class PlannerTest {

    private static Action reachDestination;
    private static Set<ActionNode> pathForGoal;
    private static int expectedTotalCost;
    private static int expectedNumberOfNodes;

    @BeforeClass
    public static void setup() {
        String hasWood = "has wood";
        String crossLake = "cross lake";
        String hasRaft = "has raft";

        expectedTotalCost = 0;
        expectedNumberOfNodes = 0;

        Action chopWood = new Action("Chop Wood", 1);
        chopWood.addEffect(hasWood);
        expectedTotalCost += chopWood.getCost();
        expectedNumberOfNodes++;

        Action lookForWood = new Action("Look For Wood", 2);
        lookForWood.addEffect(hasWood);

        Action buildRaft = new Action("Build Raft", 5);
        buildRaft.addPrecondition(hasWood);
        buildRaft.addEffect(hasRaft);
        expectedTotalCost += buildRaft.getCost();
        expectedNumberOfNodes++;

        Action crossLakeByBoat = new Action("Cross Lake By Boat", 3);
        crossLakeByBoat.addPrecondition(hasRaft);
        crossLakeByBoat.addEffect(crossLake);
        expectedTotalCost += crossLakeByBoat.getCost();
        expectedNumberOfNodes++;

        Action crossLakeBySwimming = new Action("Cross Lake By Swimming", 12);
        crossLakeBySwimming.addEffect(crossLake);

        Action goAroundTheLakeOnFoot = new Action("Go Around The Lake On Foot", 11);
        goAroundTheLakeOnFoot.addEffect(crossLake);

        reachDestination = new Action("Reach Destination", 1);
        reachDestination.addPrecondition(crossLake);
        expectedTotalCost += reachDestination.getCost();
        expectedNumberOfNodes++;

        Planner planner = new Planner();

        List<Action> actions = asList(
                chopWood, lookForWood, buildRaft, crossLakeByBoat,
                crossLakeBySwimming, goAroundTheLakeOnFoot, reachDestination);

        pathForGoal = planner.getPathForGoal(actions, reachDestination);
    }

    @Test
    public void can_reach_destination_test() {
        assertFalse("Could not find path", pathForGoal.isEmpty());
        Optional<Action> destinationAction = pathForGoal.stream()
                .map(ActionNode::getAction)
                .skip(pathForGoal.size() - 1)
                .findAny();
        if (destinationAction.isPresent()) {
            assertEquals(reachDestination, destinationAction.get());
        } else {
            fail("Could not find path");
        }
    }

    @Test
    public void number_of_nodes_test() {
        assertEquals(expectedNumberOfNodes, pathForGoal.size());
    }

    @Test
    public void fastest_path_test() {
        Optional<ActionNode> destinationAction = pathForGoal.stream()
                .filter(actionNode -> actionNode.getAction().equals(reachDestination))
                .findFirst();
        if (destinationAction.isPresent()) {
            Integer pathCost = destinationAction.get().getTotalCost();
            assertEquals(expectedTotalCost, pathCost.intValue());
        } else {
            fail("Could not find path");
        }
    }
}
