package gitlet;

import com.google.common.cache.AbstractCache;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
    public static final File HEADS = join(GITLET_DIR, "heads");
    /** staging directory that contain staging for adding and staging for removing .*/
    public static  File StagingAreaDir = join(GITLET_DIR, "stagingArea");
    /** staging for adding directory .*/
    public static File StagingForAdding = join(StagingAreaDir, "stagingForAdding");
    /** staging for removing directory. */
    public static File StagingForRemoving= join(StagingAreaDir, "stagingForRemoving");
    public static StagingArea StagingArea = new StagingArea();
    public static Blob Blob=new Blob();
    public static String head =null;
    public static String parent ;
   private static Map<String,File> tracked = new HashMap<>();
    /** printing error message. */
    public static void errorMessage(String message) {
        System.err.println(message);
        System.exit(1);
    }
    public static void setupPersistence(){
        GITLET_DIR.mkdirs();
        COMMIT_DIR.mkdirs();
        BLOBS_DIR.mkdirs();
        StagingAreaDir.mkdirs();
        StagingForAdding.mkdirs();
        StagingForRemoving.mkdirs();
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

    /**  init command */
    public static void init() {
        if (isInitialized()) {
            errorMessage("A Gitlet version-control system already exists in the current directory.");
        }
        setupPersistence();
        Commit commit = new Commit();
        String headName =commit.saveCommit();
        setHead(headName);
        // TODO: somehow make master branch
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
    /**
     * ToDo:verify is the file exist to the staging area
     * todo:if the file is already staged overwrite it
     * todo:if the current working version of the file is identical to the version in the current commit
     *      do not stage it to be added and remove it from staging area if already there
     * */
    public static void add(String FileName){

            File f = new File(CWD,FileName);
            if(FileExistInCWD(f)){
                // fileSha is the name of the blob
                String fileSha=Utils.sha1(f);
                if(!TheSameAsTheCurrentCommit(fileSha)) {
                    StagingArea.addFileToStagingArea(f, fileSha);
                }

            }else{
               errorMessage("File does not exist.");
            }
    }
    /**
     * todo:load the content of the parent commit
     * todo:remove the files that staged for removal by rm command
     * todo:add the files that staged to be add
     * todo:after commit clear staging area
     * todo:commit is added as a new node in the commit tree
     * todo:set the head pointer to point to this commit
     * todo:the parent head should points to current head
     * Each commit is identified by its SHA-1 id, which must include the file (blob)
     * references of its files, parent reference, log message, and commit time.
     * */
    public static void Commit(String message) {
        if (message == null) {
            errorMessage("Please enter a commit message.");
        }
        // get the files than last commit trake
        // get the files from staging file
        // remove from them the files tha staged to be removed
        copyTheLastCommitTrackedFiles();
        copyFilesFromStagingArea();
        removeFilesThatStagedTobeRemoved();
        parent = getHead();

        Commit commit = new Commit(message, parent, tracked);
        String newHead = commit.saveCommit();
        Blob.addBlobs(tracked);
        StagingArea.clear();
        setHead(newHead);

    }
   public static void copyTheLastCommitTrackedFiles(){
        String commitName=getHead();
        File f=Utils.join(COMMIT_DIR,commitName);
        Commit lastCommit=Utils.readObject(f, Commit.class);
       tracked.putAll(lastCommit.getTrackedFiles());
   }
   public static void copyFilesFromStagingArea(){
        File[] files=StagingArea.getStagedFiles();
        for(File f:files){
            tracked.put(f.getName(),f);
        }
   }
   public static void removeFilesThatStagedTobeRemoved(){
        File[] files = StagingArea.getStagedToBeRemoved();
        if(files!=null) {
            for (File f : files) {
                tracked.remove(f.getName());
            }
        }
   }


    /** function to make sure that we initialize a git let directory. */
    public static boolean isInitialized(){
        File f=new File(CWD, ".gitlet");
        return f.exists();
    }
    /** function that return the head of the current branch. */
    public static String getHead(){
        File f=new File(GITLET_DIR, "heads");
        //TODO: complete this function
        if(head==null){
            return head=Utils.readContentsAsString(f);
        }
        return head;
    }
    /** updating the head. */
    public static void setHead(String name){
        File f=new File(GITLET_DIR, "head");
        Utils.writeContents(f,name);
    }
    /** Knowing if the current working version of the file
     * is identical to the version in the current commit
     */
    public static boolean TheSameAsTheCurrentCommit(String fileName){
        //TODO:some how do this
        return false;
    }

    /** To Know if the file exist in the current working directory or not*/
    public static boolean FileExistInCWD(File file){
        return file.exists();
    }

    /* TODO: fill in the rest of this class. */
}
