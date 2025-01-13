package gitlet;

import java.io.File;

public class StagingArea {

    public static File StagingAreaDir=Repository.StagingAreaDir;
    public static File StagingForAdding=Repository.StagingForAdding;
    public static File StagingForRemoving=Repository.StagingForRemoving;

    public  static void addFileToStagingArea(File file,String fileName) {
        // if already exist i override its content
            File stagedFile= new File(StagingForAdding,fileName);
            Utils.writeContents(stagedFile,Utils.readContentsAsString(file));

    }
    public  static void StageForRemoving(File file) {
        File stagedFile= new File(StagingForRemoving,file.getName());
        Utils.writeContents(stagedFile,Utils.readContentsAsString(file));
    }

    public  File[] getStagedFiles(){
        File[] files= StagingForAdding.listFiles();
        return files;
    }
    public File[] getStagedToBeRemoved(){
        File[] files= StagingForRemoving.listFiles();
        return files;
    }

    /**
     * Deletes all files in the StagingForAdding and StagingForRemoving directories.
     */
    public void clear() {
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
