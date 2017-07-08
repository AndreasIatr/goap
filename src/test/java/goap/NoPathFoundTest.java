package goap;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.fail;

public class NoPathFoundTest {

    @Test
    public void no_actions_results_in_exception_test() {
        Planner<String> planner = new Planner<>();
        try {
            planner.getPlan(Collections.emptySet(), "goal", Collections.emptyList());
            fail();
        } catch (PathToGoalNotFoundException e) {
            // should happen
        }
    }

    @Test
    public void could_not_find_path_to_goal_test() {
        Planner<String> planner = new Planner<>();

        Action<String> action1 = new Action<>("Action1", 1);
        Action<String> action2 = new Action<>("Action2", 2);

        Set<Action<String>> actions = Sets.newSet(action1, action2);

        try {
            planner.getPlan(actions, "Some impossible precondition", Collections.emptyList());
            fail();
        } catch (PathToGoalNotFoundException e) {
            // should happen
        }

    }
}
