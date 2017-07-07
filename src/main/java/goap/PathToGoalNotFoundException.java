package goap;

public class PathToGoalNotFoundException extends Exception {
    public PathToGoalNotFoundException(Object goal) {
        super("Could not find path to goal " + goal);
    }
}
