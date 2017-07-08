package goap;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OpenDoorTest {

    private static LinkedList<ActionNode<String>> pathForGoalNoState, pathForGoalWithState;
    private static Action<String> openDoor;
    private static int expectedTotalCost, expectedNumberOfNodes;
    private static String doorIsOpen;

    @BeforeClass
    public static void setup() throws PathToGoalNotFoundException {
        String hasKey = "has key";
        String doorIsLocked = "door is locked";
        String doorIsUnlocked = "door is unlocked";
        doorIsOpen = "door is open";
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

        Set<Action<String>> actions = Sets.newSet(attemptToOpenDoor, lookForKey, unlockDoor, openDoor,
                bashDoor, breakDownDoor);

        Planner<String> planner = new Planner<>();

        ArrayList<String> state = new ArrayList<>();

        pathForGoalNoState = planner.getPlan(actions, doorIsOpen, state);

        state.add(doorIsUnlocked);
        pathForGoalWithState = planner.getPlan(actions, doorIsOpen, state);
    }

    @Test
    public void can_reach_goal_no_state_test() {
        assertTrue(pathForGoalNoState.peekLast().getAction().getEffects().contains(doorIsOpen));
    }

    @Test
    public void can_reach_goal_with_state_test() {
        assertTrue(pathForGoalWithState.peekLast().getAction().getEffects().contains(doorIsOpen));
    }

    @Test
    public void number_of_nodes_no_state_test() {
        assertEquals(expectedNumberOfNodes, pathForGoalNoState.size());
    }

    @Test
    public void number_of_nodes_with_state_test() {
        assertEquals(openDoor.getCost(), pathForGoalWithState.size());
    }

    @Test
    public void fastest_path_no_state_test() {
        assertEquals(expectedTotalCost, pathForGoalNoState.peekLast().getTotalCost());
    }

    @Test
    public void fastest_path_with_state_test() {
        assertEquals(openDoor.getCost(),
                pathForGoalWithState.peekLast().getTotalCost());
    }
}
