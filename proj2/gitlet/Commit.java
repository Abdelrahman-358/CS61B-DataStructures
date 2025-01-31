package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author Abdelrahman Mostafa
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /**
     * The message of this Commit.
     */
    private final String message;
    /**
     * the date of this commit.
     */
    private final Date date;
    /**
     * parent of the commit.
     */
    private final String parent;
    /**
     * that contains the referencing files.
     */
    // Map Store Name -> sha
    private final Map<String, String> trackByName;
    // Map Store sha -> Name
    private final Map<String, String> trackBySha;

    /**
     * initial commit
     */
    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.parent = "stop";
        this.trackByName = new TreeMap<>();;
        this.trackBySha = new TreeMap<>();;

    }

    public Commit(String message, String parent, Map<String, String> tracked) {
        if (message == null) {
            Repository.errorMessage("Please enter a commit message.");
        }
        this.message = message;
        this.date = new Date();
        this.parent = parent;
        // tacked sha -> file
        this.trackBySha = new TreeMap<>();;
        this.trackByName = new TreeMap<>();;
        for (Map.Entry<String, String> entry : tracked.entrySet()) {
            trackByName.put(entry.getKey(), entry.getValue());
            trackBySha.put(entry.getValue(), entry.getKey());
        }
    }

    public String saveCommit() {

        String name = Utils.sha1(this.toString());
        File f = new File(Repository.COMMIT_DIR, name);
        Utils.writeObject(f, this);
        return name;
    }
    /**
     * Loads the files associated with a specific commit into the current working directory (CWD).
     * This function performs the following operations:
     *
     *   Retrieves the tracked files for the given commit using {@link Repository#getTrackedFilesByCommit(String)}.
     *   Retrieves the list of files currently present in the CWD using {@link Repository#getCWDFiles()}.
     *   Deletes any files in the CWD that are not part of the tracked files for the specified commit.
     *   Writes the contents of the tracked files (blobs) from the commit into the corresponding files in the CWD.
     *
     *
     * @param commitName The name of the commit whose files are to be loaded into the CWD.
     *                   This should correspond to a valid commit in the repository.
     *
     * @see Repository#getTrackedFilesByCommit(String)
     * @see Repository#getCWDFiles()
     */
    public static void loadCommitFiles(String commitName) {
        Map<String, String> wanted = Repository.getTrackedFilesByCommit(commitName);
        List<String> CWDFiles = Repository.getCWDFiles();
        for (String fileName : CWDFiles) {
            if (!wanted.containsKey(fileName)) {
                File del = new File(Repository.CWD, fileName);
                if (del.exists()) {
                    del.delete();
                }
            }
        }
        for (Map.Entry<String, String> entry : wanted.entrySet()) {
            String fileName = entry.getKey();
            String blobName = entry.getValue();

            File blobFile = new File(Repository.BLOBS_DIR, blobName);


            File newFile = new File(Repository.CWD, fileName);
            Utils.writeContents(newFile, Utils.readContentsAsString(blobFile));
        }
    }

    public static void loadFile(String fileName, String commitName) {
        Commit commit = getCommitByName(commitName);
        String blobName = commit.getTrackedFileByName(fileName);
        File file = new File(Repository.CWD, fileName);
        File blob = Blob.getFile(blobName);
        Utils.writeContents(file, Utils.readContentsAsString(blob));
    }


    /**
     * return commit object using commit name
     */
    public static Commit getCommitByName(String name) {
        File f = new File(Repository.COMMIT_DIR, name);
        Commit commit = Utils.readObject(f, Commit.class);
        return commit;
    }
    public static boolean commitExists(String commitName) {
        File f = new File(Repository.COMMIT_DIR, commitName);
        return f.exists();
    }

    /**
     * getters
     */
    public String getTrackedFileByName(String name) {
        return this.trackByName.get(name);
    }
    public String getMessage() {
        return this.message;
    }

    public Date getDate() {
        return this.date;
    }

    public String getParent() {
        return this.parent;
    }

    public Map<String, String> getTrackByName() {
        return this.trackByName;
    }

    public Map<String, String> getTrackBySha() {
        return this.trackBySha;
    }

    public boolean isFileTracked(String fileName) {
        return this.trackByName.containsKey(fileName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Message: ").append(message).append("\n");
        sb.append("Date: ").append(date).append("\n");
        sb.append("Parent: ").append(parent).append("\n");
        sb.append("Tracked Files:\n");
        for (Map.Entry<String, String> entry : trackBySha.entrySet()) {
            sb.append("  ").append(entry.getKey()).append("\n");
        }
        for (Map.Entry<String, String> entry : trackByName.entrySet()) {
            sb.append("  ").append(entry.getKey()).append("\n");
        }
        return sb.toString();
    }


}
