package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author Abdelrahman Mostafa
 */
public class Repository implements Serializable {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * commits directory
     */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    /**
     * blobs directory
     */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    /**
     * Head File
     */
    public static File HEAD = join(GITLET_DIR, "heads");
    /**
     * staging directory that contain staging for adding and staging for removing .
     */
    public static File StagingAreaDir = join(GITLET_DIR, "stagingArea");
    /**
     * staging for adding directory .
     */
    public static File StagingForAdding = join(StagingAreaDir, "stagingForAdding");
    /**
     * staging for removing directory.
     */
    public static File StagingForRemoving = join(StagingAreaDir, "stagingForRemoving");
    // public static StagingArea StagingArea = new StagingArea();
    public static String head = "no";
    public static String parent;
    /**
     * track the loaded commits applying lazy load and cashing
     */
    private static Map<String, Integer> isLoaded = new HashMap<>();
    private static Map<String, String> trackedByName = new HashMap<>();
    /** ------------------------------------------------------------init command----------------------------------------- */

    /**
     * Initializes a new Gitlet version-control system in the current directory.
     * This system starts with one commit: the initial commit, which contains no files and has the commit message
     * "initial commit". It creates a single branch called `master`, which initially points to this initial commit.
     * The `master` branch is set as the current branch. The timestamp for the initial commit is set to the Unix Epoch
     * (00:00:00 UTC, Thursday, 1 January 1970). Since the initial commit is the same across all repositories created by
     * Gitlet, it will have the same UID, and all commits in all repositories will trace back to it.
     * <p>
     * ToDo:make new branch called master and point to initial commit
     */
    public static void init() {
        if (isInitialized()) {
            errorMessage("A Gitlet version-control system already exists in the current directory.");
        }
        setupPersistence();

        Commit initialCommit = new Commit();
        String headName = initialCommit.saveCommit();
        // at the initial commit the head and its parent is the same thing
        setHead(headName);
        parent = getHead();

        // TODO: somehow make master branch
    }
    /**--------------------------------------------------------------------------add command-----------------------------*/
    /**
     * Adds a copy of the file as it currently exists to the staging area (also called staging the file for addition).
     * Staging an already-staged file overwrites the previous entry in the staging area with the new contents.
     * The staging area should be located somewhere in `.gitlet`. If the current working version of the file is
     * identical to the version in the current commit, the file is not staged for addition and is removed from
     * the staging area if it is already there. Additionally, if the file was staged for removal, it is no longer
     * staged for removal after this command.
     *
     * @param fileName The name of the file to be staged for addition.
     */
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
            System.out.println("exist");
            StagingArea.unstageFromAdd(fileName);
            StagingArea.unstageFromRemove(fileName);

        } else {
            StagingArea.stageForAdd(file, fileName);
        }

    }
    /**-----------------------------------------------------------------commit command-----------------------------------*/
    /**
     * Creates a new commit with the provided message. This commit:
     * - Loads the content of the parent commit.
     * - Removes files that are staged for removal (via the `rm` command).
     * - Adds files that are staged for addition.
     * - Clears the staging area after the commit.
     * - Adds the commit as a new node in the commit tree.
     * - Sets the head pointer to point to this new commit.
     * - Updates the parent of the new commit to point to the current head.
     * <p>
     * Each commit is identified by its SHA-1 ID, which includes references to its files (blobs),
     * parent reference, log message, and commit time.
     *
     * @param message The commit message describing the changes made in this commit.
     */
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

        Commit commit = new Commit(message, parent, trackedByName);
        String newHead = commit.saveCommit();

        Blob.saveBlobs(StagingArea.getStagedForAdding());

        setHead(newHead);

        StagingArea.clear();

    }
    /**----------------------------------------------------------------------------rm command----------------------------*/
    /**
     * Unstages the file if it is currently staged for addition. If the file is tracked in the current commit,
     * stages it for removal and removes the file from the working directory (unless the user has already done so).
     * The file is only removed if it is tracked in the current commit.
     *
     * @param fileName The name of the file to be removed or unstaged.
     */
    public static void rm(String fileName) {
        if (StagingArea.isStagedForAdding(fileName)) {
            StagingArea.unstageFromAdd(fileName);
        } else {
            if (trackedByCurrentCommit(fileName)) {
                StagingArea.stageForRemove(fileName, trackedByName.get(fileName));
                removeFileFromCWD(fileName);
            }
        }
    }
    /**----------------------------------------------------------------------------log command---------------------------*/
    /**
     * Starting at the current head commit, displays information about each commit backwards along the commit tree
     * until the initial commit. This follows the first parent commit links, ignoring any second parents found in merge
     * commits (similar to `git log --first-parent` in regular Git). This set of commit nodes is called the commit’s history.
     * For every node in this history, the information displayed includes the commit ID, the time the commit was made,
     * and the commit message.:
     */
    public static void log() {
        String current = getHead();
        while (true) {
            Commit commit = Commit.getCommitByName(current);
            printCommit(current, commit.getMessage(), commit.getDate());
            String par = commit.getParent();
            if (par.equals("stop") || current.equals(par)) {
                break;
            }
            current = par;
        }
    }
    /**--------------------------------------------------------------------------- global-log------------------------*/
    /**
     * Displays the log of all commits in the repository. For each commit, it prints the commit ID,
     * commit message, and commit date in a formatted manner.
     */
    public static void global_log() {
        List<String> ls = Utils.plainFilenamesIn(COMMIT_DIR);
        for (String s : ls) {
            Commit commit = Commit.getCommitByName(s);
            printCommit(s, commit.getMessage(), commit.getDate());
        }
    }
    /**---------------------------------------------------------------------------find-----------------------------------*/
    /**
     * Searches for and prints all commits in the `COMMIT_DIR` directory that match the given commit message.
     * If no commits with the specified message are found, an error message is displayed.
     *
     * @param message The commit message to search for. This is a case-sensitive string.
     */
    public static void find(String message) {
        List<String> ls = Utils.plainFilenamesIn(COMMIT_DIR);

        boolean found = false;

        for (String s : ls) {
            Commit commit = Commit.getCommitByName(s);

            if (commit.getMessage().equals(message)) {
                found = true;
                printCommit(s, commit.getMessage(), commit.getDate());
            }
        }

        if (!found) {
            errorMessage("Found no commit with that message.");
        }
    }
    /**---------------------------------------------------------------------------status---------------------------------*/
    /**
     * * Displays the current status of the repository, including:
     * * - Existing branches, with the current branch marked by an asterisk (*).
     * * - Files staged for addition.
     * * - Files staged for removal.
     * * - Modifications not staged for commit.
     * * - Untracked files.
     * *
     * * The output follows a specific format to clearly present the repository's state
     */
    public static void status() {
        printBranches();

        printStagedFiles();

        printRemovedFiles();

        printModificationsNotStagedForCommit();

        printUntrackedFiles();
    }
    /**--------------------------------------------------------------------------- checkout------------------------------*/
    /**
     * Checkout is a kind of general command that can do a few different things depending on what its arguments are.
     * There are 3 possible use cases. In each section below, you’ll see 3 numbered points. Each corresponds to the
     * respective usage of checkout.
     * <p>
     * java gitlet.Main checkout -- [file name]
     * <p>
     * java gitlet.Main checkout [commit id] -- [file name]
     * <p>
     * java gitlet.Main checkout [branch name]
     * Descriptions:
     * <p>
     * Takes the version of the file as it exists in the head commit and puts it in the working directory, overwriting
     * the version of the file that’s already there if there is one. The new version of the file is not staged.
     * <p>
     * Takes the version of the file as it exists in the commit with the given id, and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one. The new version of the file is not staged.
     * <p>
     * Takes all files in the commit at the head of the given branch, and puts them in the working directory, overwriting
     * the versions of the files that are already there if they exist. Also, at the end of this command, the given branch
     * will now be considered the current branch (HEAD). Any files that are tracked in the current branch but are not present
     * in the checked-out branch are deleted. The staging area is cleared, unless the checked-out branch is the current branch
     * (see Failure cases below).
     * Failure cases
     * 1: If the file does not exist in the previous commit, abort, printing the error message File does not exist in that commit. Do not change the CWD.
     */
    public static void checkout(String fileName) {
        if(!trackedByCurrentCommit(fileName)) {
            errorMessage("File does not exist in that commit.");
        }else{
            loadFileFromCommit(fileName,getHead());
        }
    }
    public static void checkout(String commitName, String fileName) {
            if(!Commit.commitExists(commitName)) {
                errorMessage("File does not exist in that commit.");
            }else if(!trackedByCurrentCommit(fileName)) {
                errorMessage("File does not exist in that commit.");
            }else {
                loadFileFromCommit(fileName,commitName);
            }
    }
    public static void checkoutBranch(String branchName) {

    }


    /**
     * --------------------------------------------------------------------------- helper methods------------------------
     */
    /**
     * Loads the contents of a file from a specific commit into the current working directory.
     *
     * This method retrieves the specified file from the given commit and writes its contents
     * to a file with the same name in the current working directory. If the file already exists,
     * it will be overwritten with the contents from the commit.
     *
     * @param fileName    The name of the file to be loaded from the commit.
     * @param commitName  The name (or hash) of the commit from which the file is to be loaded.
     */
    public static void loadFileFromCommit(String fileName,String commitName) {
        Commit commit = Commit.getCommitByName(commitName);
        String blobName = commit.getTrackedFileByName(fileName);
        File file=new File(CWD,fileName);
        File blob=Blob.getFile(blobName);
        Utils.writeContents(file, Utils.readContentsAsString(blob));
    }

    public static void debugging(){
        System.out.println("head");
        System.out.println(getHead());
        Commit commit = Commit.getCommitByName(getHead());
        System.out.println(commit.getMessage());
        Map<String,String>m,n=new HashMap<>();
        m=commit.getTrackByName();
        n=commit.getTrackBySha();

        System.out.println(m.values());
        System.out.println(n.values());

    }

    // TODO:fill out this function
    public static void printBranches() {
        System.out.println("=== Branches ===");
        System.out.println();
    }

    public static void printStagedFiles() {
        System.out.println("=== Staged Files ===");
        File[] files = StagingArea.getStagedForAdding();
        for (File f : files) {
            System.out.println(f.getName());
        }
        System.out.println();
    }

    public static void printRemovedFiles() {
        System.out.println("=== Removed Files ===");
        File[] files = StagingArea.getStagedToBeRemoved();
        for (File f : files) {
            System.out.println(f.getName());
        }
        System.out.println();
    }

    // TODO:fill out this function
    public static void printModificationsNotStagedForCommit() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
    }

    // TODO:fill out this function
    public static void printUntrackedFiles() {
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }
    /**
     * Knowing if the file with this name is tracked by current commit or not
     * @return true if yes false otherwise
     * */
    public static boolean trackedByCurrentCommit(String fileName) {
        copyTheLastCommitTrackedFiles();
        return trackedByName.containsKey(fileName);

    }
    public static boolean trackedByCommit(String commitName, String fileName) {
        Commit commit=Commit.getCommitByName(commitName);
        return commit.isFileTracked(fileName);
    }

    public static void copyTheLastCommitTrackedFiles() {
        if (isLoaded.containsKey(getHead())) {
            return;
        }
        isLoaded.put(getHead(), 1);
        String commitName = getHead();
        File f = Utils.join(COMMIT_DIR, commitName);
        Commit lastCommit = Utils.readObject(f, Commit.class);

        Map<String, String> trackBySha = lastCommit.getTrackBySha();
        trackedByName = lastCommit.getTrackByName();

    }
    public static void copyFilesFromStagingArea() {

        File[] files = StagingArea.getStagedForAdding();
        if (files == null || files.length == 0) {
            errorMessage("No changes added to the commit.");
        }
        for (File f : files) {
            String contentSha = sha1(toString(f));

            trackedByName.put(f.getName(),contentSha);

        }
    }

    public static void printCommit(String name, String message, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        String formattedDate = dateFormat.format(date);
        System.out.println("===");
        System.out.println("commit " + name);
        System.out.println(formattedDate);
        System.out.println(message);
        System.out.println();
    }



    public static void removeFilesThatStagedTobeRemoved() {
        File[] files = StagingArea.getStagedToBeRemoved();
        if (files != null) {
            for (File f : files) {
                trackedByName.remove(f.getName());
            }
        }
    }


    /**
     * printing error message.
     */
    public static void errorMessage(String message) {
        System.err.println(message);
        System.exit(1);
    }

    /**
     * Sets up the directory structure and files required.
     */
    public static void setupPersistence() {
        GITLET_DIR.mkdirs();
        COMMIT_DIR.mkdirs();
        BLOBS_DIR.mkdirs();
        StagingAreaDir.mkdirs();
        StagingForAdding.mkdirs();
        StagingForRemoving.mkdirs();
        try {
            HEAD.createNewFile();

        } catch (IOException e) {
            errorMessage("Error creating head");
        }
    }

    /**
     * function that return the head of the current branch.
     */
    public static String getHead() {
        // Check if the head is already cached
        if (head.equals("no")) {
            head = Utils.readContentsAsString(HEAD);
        }
        return head;
    }

    /**
     * updating the head.
     */
    public static void setHead(String name) {
        File file = new File(GITLET_DIR,"heads");
        Utils.writeContents(file, name);
        head = name;
    }

    /**
     * Knowing if the current working version of the file
     * is identical to the version in the current commit
     */
    public static boolean isTheSameAsTheCurrentCommit(String fileName) {
        copyTheLastCommitTrackedFiles();
        File file = new File(CWD, fileName);
        String contentSha = sha1(toString(file));
        return (contentSha.equals(trackedByName.get(fileName)));
    }

    /**
     * function to check if we initialize a git let directory.
     */
    public static boolean isInitialized() {
        return GITLET_DIR.exists();
    }

    // removing file from current working directory
    public static void removeFileFromCWD(String fileName) {
        File file = new File(CWD, fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * To Know if the file exist in the current working directory or not
     */
    public static boolean fileExistInCWD(File file) {
        return file.exists();
    }

    public static String toString(File file) {
        return (Utils.readContentsAsString(file) + file.getName());
    }

}
