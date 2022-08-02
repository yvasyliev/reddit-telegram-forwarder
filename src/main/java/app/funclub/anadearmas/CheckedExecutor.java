package app.funclub.anadearmas;

@FunctionalInterface
public interface CheckedExecutor {
    void execute() throws Exception;
}
