package gitlet;

import java.io.File;

public class StagingArea {
    public static final File StagingAreaDir = new File("StagingArea");

    StagingArea() {
        if (!StagingAreaDir.exists()) {
            StagingAreaDir.mkdir();
        }
    }

    public static void addFile(String fileName, String content) {
           /**
            * creating a file with the name equals to sh1 of the
            * file and copy the file content into is
            * */
            File file= new File(StagingAreaDir,fileName);
            Utils.writeContents(file,content);
    }
}
