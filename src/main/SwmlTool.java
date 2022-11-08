package main;

import base.ICommand;
import com.android.apksig.ApkSigner;
import com.android.apksigner.ApkSignerTool;
import params.CommandFactory;
import params.SwmlParams;

public class SwmlTool {
    public static void main(String[] args) {
        if (args.length == 0) {
//            printHelp();
            debugApkSigner();
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

    private static void debugApkSigner() {
        String[] params = new String[] { "sign",
                "--ks", "E:\\IntelliJProject\\swmlTool\\ext\\bigcat.keystore",
                "--ks-key-alias", "bigcat",
                "--ks-pass", "pass:bigcat",
                "--v1-signing-enabled", "true",
                "--v2-signing-enabled", "true",
                "--v3-signing-enabled", "true",
                "--in", "E:\\IntelliJProject\\swmlTool\\ext\\app-unsigned.apk",
                "--out", "E:\\IntelliJProject\\swmlTool\\ext\\app-v2.apk"};
        try {
            ApkSignerTool.main(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
