package gitlet;

import java.io.File;
import java.util.Map;

public class Blob {
       public static File BlobDirectory=Repository.BLOBS_DIR;


    public void addBlobs(Map<String, File> filesToBeAdded) {
        for (Map.Entry<String, File> entry : filesToBeAdded.entrySet()) {
            String FileName = entry.getKey(); // Filename
            File file = entry.getValue(); // File

            File blobFile = new File(BlobDirectory, FileName);

            if (!blobFile.exists()) {
                Utils.writeContents(blobFile, Utils.readContentsAsString(file));

            }
        }
    }
}
