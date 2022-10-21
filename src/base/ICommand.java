package base;

public interface ICommand {
    int execute(String... args);

    int TASK_SUCCESS = 1000;
    int TASK_FAILURE = 1001;
}
