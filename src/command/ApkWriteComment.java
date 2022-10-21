package command;

import base.ICommand;
import params.SwmlParams;
import util.ConvertUtils;

import java.io.*;
import java.util.zip.ZipFile;

public class ApkWriteComment implements ICommand {
    @Override
    public int execute(String... args) {
        String originPath = SwmlParams.getParams(SwmlParams.ORIGIN_PATH);
        String commentStr = SwmlParams.getParams(SwmlParams.COMMENT_STR);
        String targetPath = SwmlParams.getParams(SwmlParams.TARGET_PATH);

        if (originPath == null || originPath.isEmpty()) {
            System.out.println("Origin file is null");
            return ICommand.TASK_FAILURE;
        }

        if (commentStr == null || commentStr.isEmpty()) {
            System.out.println("Comment string is null");
            return ICommand.TASK_FAILURE;
        }

        if (targetPath == null) {
            targetPath = "";
        }

        File originFile = new File(originPath);
        if (!originFile.exists()) {
            System.out.println("File is no exist");
            return ICommand.TASK_FAILURE;
        }

        writeComment(originFile, commentStr, targetPath);

        return ICommand.TASK_SUCCESS;
    }

    private void writeComment(File apkFile, String comment, String outFile) {
        ZipFile zipFile = null;
        ByteArrayOutputStream outs = null;
        RandomAccessFile accessFile = null;
        try {
            zipFile = new ZipFile(apkFile);
            String zipComment = zipFile.getComment();

            StringBuilder newSb;
            if (zipComment != null) {
                newSb = new StringBuilder(zipComment);
                newSb.append(comment);
                System.out.println(String.format("This file has comment [%s] first", zipComment));
            } else {
                newSb = new StringBuilder(comment);
            }

            byte[] newCommentByte = newSb.toString().getBytes();
            outs = new ByteArrayOutputStream();

            outs.write(newCommentByte);
            outs.write(ConvertUtils.short2ByteArr((short) newCommentByte.length));

            byte[] newCommentWithLenByte = outs.toByteArray();

            StringBuilder fixPathBuilder = new StringBuilder(outFile);
            if (outFile.isEmpty()) {
                String parentPath = getAbsPath();
                fixPathBuilder.append(parentPath);
                fixPathBuilder.append(File.separator);

                fixPathBuilder.append(comment);
                String originFileName = apkFile.getName();
                fixPathBuilder.append(originFileName);
            }

            System.out.println(String.format("Target path = %s", fixPathBuilder.toString()));
            File targetFile = copyFile(apkFile, fixPathBuilder.toString());
            if (targetFile.exists()) {
                accessFile = new RandomAccessFile(apkFile, "rw");
                accessFile.seek(apkFile.length() - 2);
                accessFile.write(ConvertUtils.short2ByteArr((short) newCommentWithLenByte.length));
                accessFile.write(newCommentWithLenByte);
            } else {
                System.out.println("Target File create failure");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }

                if (outs != null) {
                    outs.close();
                }

                if (accessFile != null) {
                    accessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private File copyFile(File originFile, String targetFilePath) {
        File targetFile = new File(targetFilePath);
        if (targetFile.exists()) {
            System.out.println("Target File is exit");
        } else {
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStream ins = null;
        OutputStream outs = null;
        if (targetFile.exists()) {
            try {
                ins = new FileInputStream(originFile);
                outs = new FileOutputStream(targetFile);

                byte[] fileByte = new byte[1024];
                int readLen;
                while ((readLen = ins.read(fileByte)) > 0) {
                    outs.write(fileByte, 0, readLen);
                    outs.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ins != null) {
                        ins.close();
                    }
                    if (outs != null) {
                        outs.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return targetFile;
    }

    private String getAbsPath() {
        File pwd = new File("");
        return pwd.getAbsolutePath();
    }
}
