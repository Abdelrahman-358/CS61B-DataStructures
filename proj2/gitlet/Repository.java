package gitlet;


import java.io.File;
import java.io.IOException;
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
    public static File HEAD = join(GITLET_DIR, "heads");
    /** staging directory that contain staging for adding and staging for removing .*/
    public static  File StagingAreaDir = join(GITLET_DIR, "stagingArea");
    /** staging for adding directory .*/
    public static File StagingForAdding = join(StagingAreaDir, "stagingForAdding");
    /** staging for removing directory. */
    public static File StagingForRemoving= join(StagingAreaDir, "stagingForRemoving");
   // public static StagingArea StagingArea = new StagingArea();
    public static String head =null;
    public static String parent ;
    // Map to store BlobName -> and the fileName
    private static Map<String,String> tracked = new HashMap<>();
    /** track the loaded commits */
    private static Map<String,Integer>isLoaded = new HashMap<>();
    private static Map<String,String>trackByName= new HashMap<>();
    /** ------------------------------------------------------------init command----------------------------------------- */

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
     * ToDo:make new branch called master and point to initial commit
     */
    public static void init() {
        if (isInitialized()) {
            errorMessage("A Gitlet version-control system already exists in the current directory.");
        }
        setupPersistence();

        Commit initialCommit = new Commit();
        String headName =initialCommit.saveCommit();
        // at the initial commit the head and its parent is the same thing
        setHead(headName);
        parent=getHead();

        // TODO: somehow make master branch
    }
    /**--------------------------------------------------------------------------add command-----------------------------*/
    /**
     *  Description: Adds a copy of the file as it currently exists to the staging area
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
     * todo:The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
     * */
    public static void add(String fileName) {

        File file = new File(CWD, fileName);
        if (!file.exists()) {
            errorMessage("File does not exist.");
        }
        // fileSha is the name of the blob
        // at staging area the file stored with its original name
        // we compare the sha1 of the file and existing file at current commit

        if (isTheSameAsTheCurrentCommit(fileName)) {
            // remove it from staging area
            // and if staged for removal unstage in
            StagingArea.removeFileFromStagingForAdding(fileName);
            StagingArea.removeFileFromStagingForRemoving(fileName);

        } else {
            StagingArea.addFileToStagingForAdding(file, fileName);
        }

    }
    /**-----------------------------------------------------------------commit command-----------------------------------*/
    /**
     * :load the content of the parent commit
     * :remove the files that staged for removal by rm command
     * :add the files that staged to be added
     * :after commit clear staging area
     * :commit is added as a new node in the commit tree
     * :set the head pointer to point to this commit
     * :the parent head should points to current head
     * Each commit is identified by its SHA-1 id, which must include the file (blob)
     * references of its files, parent reference, log message, and commit time.
     * */
    public static void commit(String message) {
        if (message == null) {
            errorMessage("Please enter a commit message.");
        }
        // get the files from staging file
        // remove from them the files tha staged to be removed
        copyTheLastCommitTrackedFiles();

        copyFilesFromStagingArea();
        removeFilesThatStagedTobeRemoved();

        parent = getHead();

        Commit commit = new Commit(message, parent, tracked);
        String newHead = commit.saveCommit();

        Blob.addBlobs(StagingArea.getStagedFiles());

        StagingArea.clear();
        setHead(newHead);

    }
    /**----------------------------------------------------------------------------rm command----------------------------*/
    /**
     * Unstage the file if it is currently staged for addition
     * If the file is tracked in the current commit, stage it
     * for removal and remove the file from the working directory
     * if the user has not already done so (do not remove it unless
     * it is tracked in the current commit).
     * */

    public static void rm(String fileName) {
        if(StagingArea.isStagedForAdding(fileName)){
            StagingArea.removeFileFromStagingForAdding(fileName);
        }else{
            if(trackedByCurrentCommit(fileName)){
                StagingArea.addFileToStagingForRemoving(fileName,trackByName.get(fileName));
                removeFileFromCWD(fileName);
            }
        }
    }
    /**--------------------------------------------------------------------------- helper methods------------------------*/
    public static boolean trackedByCurrentCommit(String fileName) {
        copyTheLastCommitTrackedFiles();
        return trackByName.containsKey(fileName);

    }
   public static void copyTheLastCommitTrackedFiles(){
       if(isLoaded.containsKey(getHead())){
           return;
       }
       isLoaded.put(getHead(), 1);
        String commitName=getHead();
        File f=Utils.join(COMMIT_DIR,commitName);
        Commit lastCommit=Utils.readObject(f, Commit.class);

        Map<String,String>trackBySha=lastCommit.getTrackBySha();
        trackByName=lastCommit.getTrackByName();

        // key is the sha of the content of the file with this name(value)
       for(Map.Entry<String,String> entry: trackBySha.entrySet()){
           tracked.put(entry.getKey(),entry.getValue());
       }
   }
   public static void copyFilesFromStagingArea(){

        File[] files=StagingArea.getStagedFiles();
        if(files== null || files.length == 0){
            errorMessage("No changes added to the commit.");
        }
        for(File f: files){
            String contentSha=sha1(ToString(f));
            tracked.put(contentSha,f.getName());
            
        }
   }
   public static void removeFilesThatStagedTobeRemoved(){
        File[] files = StagingArea.getStagedToBeRemoved();
        if(files!=null) {
            for (File f : files) {
                String contentSha=Utils.readContentsAsString(f);
                tracked.remove(contentSha);
            }
        }
   }


    /** printing error message. */
    public static void errorMessage(String message) {
        System.err.println(message);
        System.exit(1);
    }
    /**  Sets up the directory structure and files required. */
    public static void setupPersistence(){
        GITLET_DIR.mkdirs();
        COMMIT_DIR.mkdirs();
        BLOBS_DIR.mkdirs();
        StagingAreaDir.mkdirs();
        StagingForAdding.mkdirs();
        StagingForRemoving.mkdirs();
        try{
            HEAD.createNewFile();

        }catch (IOException e){
            errorMessage("Error creating head");
        }
    }
    /** function that return the head of the current branch. */
    public static String getHead() {
        // Check if the head is already cached
        if (head == null) {
            head = Utils.readContentsAsString(HEAD);
        }
        return head;
    }
    /** updating the head. */
    public static void setHead(String name){
        Utils.writeContents(HEAD,name);
        head=name;
    }
    /**
     * Knowing if the current working version of the file
     * is identical to the version in the current commit
     */
    public static boolean isTheSameAsTheCurrentCommit(String fileName){
        copyTheLastCommitTrackedFiles();
        File file=new File(CWD, fileName);
        String contentSha=sha1(ToString(file));
        return (tracked.containsKey(contentSha));
    }
    /** function to check if we initialize a git let directory. */
    public static boolean isInitialized(){
        return GITLET_DIR.exists();
    }

    // removing file from current working directory
    public static void removeFileFromCWD(String fileName){
        File file=new File(CWD, fileName);
        if(file.exists()){
            file.delete();
        }
    }
    /** To Know if the file exist in the current working directory or not*/
    public static boolean FileExistInCWD(File file){
        return file.exists();
    }

    public static String ToString(File file){
        return (Utils.readContentsAsString(file)+file.getName());
    }

}
