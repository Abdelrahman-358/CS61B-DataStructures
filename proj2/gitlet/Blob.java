package gitlet;

import java.io.File;
import java.util.Map;

public class Blob {
       public static File BlobDirectory=Repository.BLOBS_DIR;


    public static void addBlobs(File[] files) {
        for (File file : files) {
            String FileName = Repository.ToString(file); // Filename
            File blobFile = new File(BlobDirectory, FileName);
            if (!blobFile.exists()) {
                Utils.writeContents(blobFile, Utils.readContentsAsString(file));
            }
        }
    }

    public static File getBlobFile(String shaName){
        File file = new File(BlobDirectory, shaName);
        return file;
    }
}
