package params;

import base.ICommand;
import command.ApkReadComment;
import command.ApkWriteComment;
import command.CompareDirSimilar;

public class CommandFactory {
    private CommandFactory() {
    }

    private static CommandFactory instance;
    public static CommandFactory getInstance() {
        synchronized (CommandFactory.class) {
            if (instance == null) {
                instance = new CommandFactory();
            }
        }
        return instance;
    }

    public ICommand getExecCommand(String commandType) {
        switch (commandType) {
            case SwmlParams.READ_COMMENT:
                return new ApkReadComment();
            case SwmlParams.WRITE_COMMENT:
                return new ApkWriteComment();
            case SwmlParams.COMPARE_FILE:
                return new CompareDirSimilar();
            default:
                System.out.println("Cannot find the exec command");
                return null;
        }
    }
}
