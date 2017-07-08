package goap;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LazinessTest {

    private static Planner<String> planner;
    private static Action<String> fastAction, slowAction;
    private static String precondition = "Precondition";

    @BeforeClass
    public static void setup() {
        planner = new Planner<>();

        fastAction = new Action<>("Fast", 1);
        fastAction.addEffect(precondition);

        slowAction = new Action<>("Slow", 2);
        slowAction.addEffect(precondition);
    }

    @Test
    public void lazy_cost_test() throws PathToGoalNotFoundException {
        AtomicInteger costCalculations = new AtomicInteger(0);
        Action<String> fail = new Action<>("Fail", () -> {
            fail();
            return 10;
        });
        fail.addPrecondition("Lazy Cost Calculation Fails");

        Action<String> reachGoal = new Action<>(
                "Reach Goal",
                costCalculations::incrementAndGet);

        String goalState = "Goal State";
        reachGoal.addEffect(goalState);

        reachGoal.addPrecondition(precondition);

        LinkedList<ActionNode<String>> plan =
                planner.getPlan(
                        Arrays.asList(fail, fastAction, slowAction, reachGoal),
                        goalState,
                        Collections.emptyList());

        assertEquals(1, costCalculations.get());
        assertEquals(2, plan.getLast().getTotalCost());
    }

    @Test
    public void lazy_preconditions_test() throws PathToGoalNotFoundException {
        AtomicInteger preconditionCalculations = new AtomicInteger(0);

        Action<String> reachGoal = new Action<>("Reach Goal", 1);
        reachGoal.addPrecondition(() -> {
            preconditionCalculations.incrementAndGet();
            return precondition;
        });

        String goalState = "Goal State";
        reachGoal.addEffect(goalState);

        LinkedList<ActionNode<String>> plan =
                planner.getPlan(
                        Arrays.asList(fastAction, slowAction, reachGoal),
                        goalState,
                        Collections.emptyList());

        assertEquals(1, preconditionCalculations.get());
        assertEquals(2, plan.getLast().getTotalCost());
    }
}
