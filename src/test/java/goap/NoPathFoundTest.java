package goap;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.fail;

public class NoPathFoundTest {

    @Test
    public void no_actions_results_in_exception_test() {
        Planner<String> planner = new Planner<>();
        try {
            planner.getPlan(Collections.emptyList(), "goal", Collections.emptyList());
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

        List<Action<String>> actions = Arrays.asList(action1, action2);

        try {
            planner.getPlan(actions, "Some impossible precondition", Collections.emptyList());
            fail();
        } catch (PathToGoalNotFoundException e) {
            // should happen
        }

    }
}
