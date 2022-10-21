package main;

import base.ICommand;
import params.CommandFactory;
import params.SwmlParams;

public class SwmlTool {
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
        } else {
            parseParams(args);
            executeCommand();
        }
    }

    private static void printHelp() {
        System.out.println("Please send me params");
    }

    private static void parseParams(String[] args) {
        SwmlParams params = new SwmlParams();
        params.parseParams(args);
    }

    private static void executeCommand() {
        ICommand execCommand = CommandFactory.getInstance().getExecCommand(SwmlParams.dumpExec.toString());
        if (execCommand != null) {
            execCommand.execute();
        }
    }
}
