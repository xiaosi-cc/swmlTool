package command;

import base.ICommand;
import params.SwmlParams;

import java.io.*;
import java.util.zip.ZipFile;

public class ApkReadComment implements ICommand {
    @Override
    public int execute(String... args) {
        String apkPath = SwmlParams.getParams(SwmlParams.ORIGIN_PATH);

        if (apkPath.isEmpty()) {
            System.out.println("Apk path is empty");
            return ICommand.TASK_FAILURE;
        }

        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            System.out.println("File is not exit");
            return ICommand.TASK_FAILURE;
        }

        readComment(apkFile);
        return ICommand.TASK_SUCCESS;
    }

    private void readComment(File apkFile) {
        try {
            ZipFile zipFile = new ZipFile(apkFile);
            String zipComment = zipFile.getComment();
            if (zipComment != null) {
                System.out.println(String.format("Comment = %s", zipComment));
            } else {
                System.out.println("Comment is null");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
