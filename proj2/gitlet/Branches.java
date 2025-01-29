package gitlet;

import java.io.File;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.readContentsAsString;

public class Branches {
    public static final File BRANCH = Repository.BRANCH;
    public static String currentBranch = "no";


    public static void makeNewBranch(String branchName) {
        File file = new File(BRANCH, branchName);
        Utils.writeContents(file, Repository.getHead());
    }
    public static void loadBranch(String branchName) {

        File file = new File(BRANCH, branchName);
        String commitName = readContentsAsString(file);
        Commit.loadCommitFiles(commitName);
        updateCurrentBranch(branchName);
    }

    /**
     * Updates the reference commit for a specific branch in the repository.
     * This method writes the provided commit reference (`refCommit`) to a file
     * named after the branch (`branchName`) in the current working directory (CWD).
     *
     * @param branchName The name of the branch to update. This will be used as the filename
     *                   where the commit reference is stored. Must not be null or empty.
     * @param refCommit  The commit reference (e.g., a commit hash) to associate with the branch.
     */
    public static void updateBranch(String branchName, String refCommit) {
        File file = new File(BRANCH, branchName);
        Utils.writeContents(file, refCommit);
    }

    /**
     * Updates the current branch in the repository to the specified branch name.
     * This method writes the provided branch name (`branchName`) to a file named
     * "currentBranch" in the branch directory (`BRANCH`).
     *
     * @param branchName The name of the branch to set as the current branch. This will be
     *                   written to the "currentBranch" file. Must not be null or empty.
     */
    public static void updateCurrentBranch(String branchName) {
        File file = new File(Repository.GITLET_DIR, "currentBranch");
        Utils.writeContents(file, branchName);
    }

    /**
     * Retrieves the name of the current branch in the repository.
     * If the current branch is not already cached (i.e., it is set to "no"),
     * this method reads the branch name from the "currentBranch" file in the
     * current working directory (CWD) and caches it for future use.
     *
     * @return The name of the current branch. This value is read from the
     * "currentBranch" file if not already cached.
     */
    public static String getCurrentBranch() {
        if (currentBranch.equals("no")) {
            File file = new File(Repository.GITLET_DIR, "currentBranch");
            currentBranch = readContentsAsString(file);
        }
        return currentBranch;
    }

    public static void removeBranch(String branchName) {
        File file = new File(BRANCH, branchName);
        file.delete();
    }

    public static boolean branchExists(String branchName) {
        File file = new File(BRANCH, branchName);
        return file.exists();
    }

    public static List<String> getBranchFiles() {
        if (BRANCH.exists()) {
            return Utils.plainFilenamesIn(BRANCH);
        }
    }

}
