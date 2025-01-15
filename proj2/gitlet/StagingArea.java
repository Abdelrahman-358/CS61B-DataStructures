package gitlet;

import java.io.File;

public class StagingArea {

    public static File StagingAreaDir=Repository.StagingAreaDir;
    public static File StagingForAdding=Repository.StagingForAdding;
    public static File StagingForRemoving=Repository.StagingForRemoving;

    public   static void addFileToStagingForAdding(File file,String fileName) {
        // if already exist i override its content
            File stagedFile= new File(StagingForAdding,fileName);
            Utils.writeContents(stagedFile,Utils.readContentsAsString(file));

    }
    public static void addFileToStagingForRemoving(String fileName,String fileContent) {
        File stagedFile= new File(StagingForRemoving,fileName);
        Utils.writeContents(stagedFile,fileContent);
    }
    public  static void removeFileFromStagingForAdding(String FileName) {
        //remove file
        File stagedFile= new File(StagingForAdding,FileName);
        if(stagedFile.exists()){
            stagedFile.delete();
        }
    }
    public static void removeFileFromStagingForRemoving(String FileName) {
        File stagedFile= new File(StagingForRemoving,FileName);
        if(stagedFile.exists()){
            stagedFile.delete();
        }
    }
    public  static void StageForRemoving(File file) {
        File stagedFile= new File(StagingForRemoving,file.getName());
        Utils.writeContents(stagedFile,Utils.readContentsAsString(file));
    }
    /**
     * return the files that staged for adding that will be saved at blobs file and been tracked by next commit
     * */
    public static File[] getStagedFiles(){
        File[] files= StagingForAdding.listFiles();
        return files;
    }

    public static File[] getStagedToBeRemoved(){
        File[] files= StagingForRemoving.listFiles();
        return files;
    }
    public static boolean isStagedForAdding(String fileName) {
        File stagedFile= new File(StagingForAdding,fileName);
        return stagedFile.exists();
    }

    /**
     * Deletes all files in the StagingForAdding and StagingForRemoving directories.
     */
    public static void clear() {
        File[] toBeAdded = getStagedFiles();
        for (File file : toBeAdded) {
            file.delete();
        }
        File[] toBeRemoved = getStagedToBeRemoved();
        for (File file : toBeRemoved) {
            file.delete();
        }

    }
}
