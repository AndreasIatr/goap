package goap;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LazinessTest {

    @Test
    public void lazy_cost_test() throws PathToGoalNotFoundException {
        AtomicInteger costCalculations = new AtomicInteger(0);
        Action<String> action = new Action<>(
                "Reach Goal",
                costCalculations::incrementAndGet);

        int cost = action.getCost();

        assertEquals(1, cost);

        action.getCost();
        assertEquals(1, costCalculations.get());

    }

    @Test
    public void lazy_preconditions_test() throws PathToGoalNotFoundException {
        AtomicInteger preconditionCalculations = new AtomicInteger(0);
        String precondition = "Precondition";

        Action<String> action = new Action<>("Reach Goal", 1);
        action.addPrecondition(() -> {
            preconditionCalculations.incrementAndGet();
            return precondition;
        });

        List<String> preconditions = action.getPreconditions();
        assertArrayEquals(new String[]{precondition}, preconditions.toArray());

        action.getPreconditions();
        assertEquals(1, preconditionCalculations.get());
    }

    @Test
    public void lazy_effects_test() throws PathToGoalNotFoundException {
        AtomicInteger effectCalculations = new AtomicInteger(0);
        String effect = "Goal State";

        Action<String> action = new Action<>("Reach Goal", 1);
        action.addEffect(() -> {
            effectCalculations.incrementAndGet();
            return effect;
        });

        List<String> effects = action.getEffects();
        assertArrayEquals(new String[]{effect}, effects.toArray());

        action.getEffects();
        assertEquals(1, effectCalculations.get());
    }
}
