package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Abdelrahman Mostafa
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** commits directory */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    /** blobs directory */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    /** Head File */
    public static final File HEAD = join(GITLET_DIR, "head");

    public static void setupPersistence(){
        GITLET_DIR.mkdirs();
        COMMIT_DIR.mkdirs();
        BLOBS_DIR.mkdirs();
        // TODO: not sure of this
        try{
            HEAD.createNewFile();
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
    /**
     * This system will automatically start with one commit:
     * a commit that contains no files and has
     * the commit message initial commit
     * It will have a single branch: master, which initially points to this initial commit
     * and master will be the current branch. The timestamp for this initial commit will be 00:00:00 UTC, Thursday,
     * 1 January 1970 in whatever format you choose for dates (this is called “The (Unix) Epoch”, represented internally
     * by the time 0.) Since the initial commit in all repositories created by Gitlet will have exactly the same content,
     * it follows that all repositories will automatically share this commit (they will all have the same UID) and all
     * commits in all repositories will trace back to it.
     */
    /**
     * ToDo:creat first commit with message initial commit
     * ToDo:make new branch called master and point to initial commit
     */
    public static void init(){
        // making wanted files
            setupPersistence();
        // creating initial commit
            Commit commit = new Commit();



    }
    /**Description: Adds a copy of the file as it currently exists to the staging area
     *  (see the description of the commit command). For this reason, adding a file is
     *  also called staging the file for addition. Staging an already-staged file overwrites
     *  the previous entry in the staging area with the new contents. The staging area should be
     *  somewhere in .gitlet. If the current working version of the file is identical to the version
     *  in the current commit, do not stage it to be added, and remove it from the staging area if
     *  it is already there (as can happen when a file is changed, added, and then changed back to
     *  it’s original version). The file will no longer be staged for removal (see gitlet rm), if
     *  it was at the time of the command.
     * */
    public static void add(String FileName){
            File f = new File(CWD,FileName);
            if(FileExistInCWD(f)){
                /** TODO: before staging this file you should check if the file
                 *        equals to the file in the last commit
                 */
                StagingArea stagingArea = new StagingArea();
                String sha=Utils.sha1(f);
                stagingArea.addFile(sha,Utils.readContentsAsString(f));
            }else{
                // print error message file not exist
            }
    }
    /** To Know if the file exist in the current working directory or not*/
    public static boolean FileExistInCWD(File file){
        return file.exists();
    }

    /* TODO: fill in the rest of this class. */
}
