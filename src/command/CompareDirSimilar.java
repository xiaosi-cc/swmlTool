package command;

import base.ICommand;
import params.SwmlParams;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

public class CompareDirSimilar implements ICommand {

    private final String[] excludePathList = {"android", "androidx", "google", "bumptech", "alibaba"};

    @Override
    public int execute(String... args) {
        String firstDir = SwmlParams.getParams(SwmlParams.FIRST_DIR);
        String secondDir = SwmlParams.getParams(SwmlParams.SECOND_DIR);
        String outFile = SwmlParams.getParams(SwmlParams.TARGET_PATH);
        String saveSamePath = SwmlParams.getParams(SwmlParams.SAME_OUT);

        if (firstDir != null && secondDir != null) {
            File firstFile = new File(firstDir);
            File secondFile = new File(secondDir);
            ArrayList<String> fileList = compareDir(firstFile, secondFile, saveSamePath);
            saveToFile(fileList, outFile);
        } else {
            System.out.println("dir is not exit");
            return ICommand.TASK_FAILURE;
        }
        return ICommand.TASK_SUCCESS;
    }

    private ArrayList<String> compareDir(File dir1, File dir2, String outFilePath) {
        ArrayDeque<File> dir1Queue = new ArrayDeque<>();
        ArrayDeque<File> dir2Queue = new ArrayDeque<>();
        dir1Queue.offer(dir1);
        dir2Queue.offer(dir2);
        File checkFile1;
        File checkFile2;

        float allFilesCount = 0f;
        float sameNameFileCount = 0f;
        float sameFileHashCount = 0f;

        Set<String> sameFilePathList = new HashSet<>();
        ArrayList<String> sameNameFileList = new ArrayList<>();
        while (!dir1Queue.isEmpty()) {
            checkFile1 = dir1Queue.poll();
            checkFile2 = dir2Queue.poll();
            if (checkFile1 != null && checkFile2 != null) {
                File[] fileList1 = checkFile1.listFiles();
                HashMap<String, File> fileList2Map = getAllFiles(checkFile2.listFiles());
                if (fileList1 != null) {
                    for (File subFile : fileList1) {
                        if (subFile == null) {
                            continue;
                        }
                        allFilesCount++;
                        if (fileList2Map.containsKey(subFile.getName())) {
                            File subFile1 = fileList2Map.get(subFile.getName());
                            if (subFile.isFile()) {
                                String hash1 = getMD5(subFile);
                                String hash2 = getMD5(subFile1);
                                if (hash1.equals(hash2)) {
                                    sameFileHashCount++;
                                    sameFilePathList.add(subFile.getAbsolutePath());
                                }
                                sameNameFileCount++;
                            } else {
                                String subFilePath = subFile.getPath();
                                if (isOutExcludePath(subFilePath)) {
                                    dir1Queue.offer(subFile);
                                    dir2Queue.offer(subFile1);
                                }
                            }
                            if (subFile.isFile()) {
                                sameNameFileList.add(subFile.getPath());
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Same Path Start >>>>>>>>>>>>>>>>>>>>>>>>>");
        for (String path : sameFilePathList) {
            System.out.println(path);
        }


        if (allFilesCount > 0) {
            float sameNamePercent = (sameNameFileCount / allFilesCount) * 100f;
            float sameFileHashPercent = (sameFileHashCount / allFilesCount) * 100f;
            float sameNameFileHashPercent = (sameFileHashCount / sameNameFileCount) * 100f;
            System.out.println("Similar Name count is = " + sameFilePathList.size() + "个");
            System.out.println("Similar Name degree is = " + sameNamePercent + "%");
            System.out.println("Similar File Md5 in all files degree is = " + sameFileHashPercent + "%");
            System.out.println("Similar File Md5 in same name file degree is = " + sameNameFileHashPercent + "%");

            sameNameFileList.add(0,"Similar File Md5 in same name file degree is = " + sameNameFileHashPercent + "%");
            sameNameFileList.add(0,"Similar File Md5 in all files degree is = " + sameFileHashPercent + "%");
            sameNameFileList.add(0,"Similar Name degree is = " + sameNamePercent + "%");
        }

        if (!outFilePath.isEmpty()) {
            System.out.println("Start write >>>>>>>>>>>>>>>>>>>>>>>>>");
            copyFile(sameFilePathList, outFilePath);
        }

        return sameNameFileList;
    }

    private boolean isOutExcludePath(String pathName) {
        for (String item : excludePathList) {
            if (pathName.contains(item)) {
                return false;
            }
        }
        return true;
    }

    private HashMap<String, File> getAllFiles(File[] fileList) {
        HashMap<String, File> dirFile = new HashMap<>();
        if (fileList == null) {
            return dirFile;
        }
        for (File subFile : fileList) {
            if (subFile == null) {
                continue;
            }
            dirFile.put(subFile.getName(), subFile);
        }
        return dirFile;
    }

    private void doFileSearch(File file) {
        ArrayDeque<File> fileQueue = new ArrayDeque<>();
        fileQueue.offer(file);
        File checkFile;

        while (!fileQueue.isEmpty()) {
            checkFile = fileQueue.poll();
            if (checkFile != null) {
                File[] fileList = checkFile.listFiles();
                if (fileList != null) {
                    for (File subFile : fileList) {
                        if (subFile == null) {
                            continue;
                        }
                        if (subFile.isFile()) {
                            System.out.println(subFile.getPath());
                        } else {
                            fileQueue.offer(subFile);
                        }
                    }
                }
            }
        }
    }

    private ArrayList<File> findSubDir(String dir1) {
        File file1 = new File(dir1);
        File[] dir1Files = file1.listFiles();

        ArrayList<File> result = new ArrayList<>();
        if (dir1Files != null) {
            for (File subFile1 : dir1Files) {
                if (subFile1.isDirectory()) {
                    result.add(subFile1);
                }
            }
        }
        return result;
    }

    private HashMap<String, String> getAllHash(File[] fileList) {
        HashMap<String, String> dir1Hash = new HashMap<>();
        if (fileList == null) {
            return dir1Hash;
        }
        for (File subFile1 : fileList) {
            if (subFile1.isDirectory()) {
                continue;
            }
            String md5 = getMD5(subFile1);
            if (md5 != null) {
                dir1Hash.put(subFile1.getName(), md5);
                System.out.println(subFile1.getName());
            }
        }
        return dir1Hash;
    }

    private String getMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            BigInteger bi = new BigInteger(1, MD5.digest());
            return bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToFile(ArrayList<String> contentList, String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        File newFile = new File(fileName);
        BufferedWriter bfw = null;
        try {
            if (newFile.exists()) {
                newFile.delete();
            } else {
                newFile.createNewFile();
            }
            bfw = new BufferedWriter(new FileWriter(newFile));
            for (String item : contentList) {
                bfw.write(item);
                bfw.newLine();
                bfw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bfw != null) {
                try {
                    bfw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void copyFile(Set<String> originPathSet, String targetPath) {
        File targetDir = new File(targetPath);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        for (String path: originPathSet) {
            File originFile = new File(path);
            File targetFile = new File(targetPath + File.separator + originFile.getName());
            if (!originFile.exists() || !originFile.isFile()) {
                continue;
            }
            try {
                if (!targetFile.exists()) {
                    targetFile.createNewFile();
                }

                BufferedInputStream buffIns = new BufferedInputStream(new FileInputStream(originFile));
                BufferedOutputStream buffOus = new BufferedOutputStream(new FileOutputStream(targetFile));
                byte[] buffer = new byte[4096]; // 4* 1024
                int read;
                while ((read = buffIns.read(buffer)) != -1) {
                    buffOus.write(buffer, 0, read);
                }
                buffOus.flush();
                buffOus.close();
                buffIns.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Write error: " + path);
            }
        }
    }
}
