import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OpenDoorTest {

    private static LinkedList<ActionNode<String>> pathForGoalNoState, pathForGoalWithState;
    private static Action<String> walkThroughDoor, openDoor;
    private static int expectedTotalCost, expectedNumberOfNodes;

    @BeforeClass
    public static void setup() {
        String hasKey = "has key";
        String doorIsLocked = "door is locked";
        String doorIsUnlocked = "door is unlocked";
        String doorIsOpen = "door is open";
        String doorBashed = "door bashed";

        Action<String> attemptToOpenDoor = new Action<>("Attempt To Open Door", 1);
        attemptToOpenDoor.addEffect(doorIsLocked);
        expectedNumberOfNodes++;
        expectedTotalCost += attemptToOpenDoor.getCost();

        Action<String> lookForKey = new Action<>("Look For Key", 4);
        lookForKey.addPrecondition(doorIsLocked);
        lookForKey.addEffect(hasKey);

        Action<String> unlockDoor = new Action<>("Unlock Door", 3);
        unlockDoor.addPrecondition(hasKey);
        unlockDoor.addEffect(doorIsUnlocked);

        openDoor = new Action<>("Open Door", 1);
        openDoor.addPrecondition(doorIsUnlocked);
        openDoor.addEffect(doorIsOpen);

        Action<String> bashDoor = new Action<>("Bash Door", 6);
        bashDoor.addPrecondition(doorIsLocked);
        bashDoor.addEffect(doorBashed);
        expectedNumberOfNodes++;
        expectedTotalCost += bashDoor.getCost();

        Action<String> breakDownDoor = new Action<>("Break Down Door", 1);
        breakDownDoor.addPrecondition(doorBashed);
        breakDownDoor.addEffect(doorIsOpen);
        expectedNumberOfNodes++;
        expectedTotalCost += breakDownDoor.getCost();

        walkThroughDoor = new Action<>("Walk Through Door", 1);
        walkThroughDoor.addPrecondition(doorIsOpen);
        expectedNumberOfNodes++;
        expectedTotalCost += walkThroughDoor.getCost();

        List<Action<String>> actions = asList(attemptToOpenDoor, lookForKey, unlockDoor, openDoor,
                bashDoor, breakDownDoor, walkThroughDoor);

        Planner<String> planner = new Planner<>();

        ArrayList<String> state = new ArrayList<>();

        pathForGoalNoState = planner.getPlan(actions, walkThroughDoor, state);

        state.add(doorIsUnlocked);
        pathForGoalWithState = planner.getPlan(actions, walkThroughDoor, state);
    }

    @Test
    public void can_reach_goal_no_state_test() {
        assertFalse("Could not find path", pathForGoalNoState.isEmpty());
        assertEquals(walkThroughDoor, pathForGoalNoState.peekLast().getAction());
    }

    @Test
    public void can_reach_goal_with_state_test() {
        assertFalse("Could not find path", pathForGoalWithState.isEmpty());
        assertEquals(walkThroughDoor, pathForGoalWithState.peekLast().getAction());
    }

    @Test
    public void number_of_nodes_no_state_test() {
        assertEquals(expectedNumberOfNodes, pathForGoalNoState.size());
    }

    @Test
    public void number_of_nodes_with_state_test() {
        assertEquals(2, pathForGoalWithState.size());
    }

    @Test
    public void fastest_path_no_state_test() {
        assertEquals(expectedTotalCost, pathForGoalNoState.peekLast().getTotalCost());
    }

    @Test
    public void fastest_path_with_state_test() {
        assertEquals(openDoor.getCost() + walkThroughDoor.getCost(),
                pathForGoalWithState.peekLast().getTotalCost());
    }
}
