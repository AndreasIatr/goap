package goap;

public class PathToGoalNotFoundException extends Exception {
    public PathToGoalNotFoundException(Action goal) {
        super("Could not find path to goal " + goal);
    }
}
