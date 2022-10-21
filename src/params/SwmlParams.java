package params;

import java.util.HashMap;

public class SwmlParams {
    private static HashMap<String, String> commandParams = new HashMap<>();
    public static StringBuilder dumpExec = new StringBuilder();

    public static String getParams(String paramsType) {
        return commandParams.getOrDefault(paramsType, "");
    }

    public void parseParams(String[] paramsArr) {
        for (int i = 0; i < paramsArr.length; ) {
            if (paramsArr[i].startsWith("-")) {
                if (i + 1 < paramsArr.length) {
                    if (paramsArr[i + 1].startsWith("-")) {
                        dumpExec.delete(0, dumpExec.length());
                        dumpExec.append(paramsArr[i]);
                        i++;
                    } else {
                        commandParams.put(paramsArr[i], paramsArr[i + 1]);
                        i += 2;
                    }
                } else {
                    dumpExec.delete(0, dumpExec.length());
                    dumpExec.append(paramsArr[i]);
                    i++;
                }
            } else {
                commandParams.put("", paramsArr[i]);
                i++;
            }
        }
    }

    public static final String ORIGIN_PATH = "-o";
    public static final String TARGET_PATH = "-t";
    public static final String COMMENT_STR = "-cmt-str";

    public static final String READ_COMMENT = "-r-cmt";
    public static final String WRITE_COMMENT = "-w-cmt";

    public static final String COMPARE_FILE = "-compare";
    public static final String FIRST_DIR = "-dir1";
    public static final String SECOND_DIR = "-dir2";
    public static final String SAME_OUT = "-same-out";
}
