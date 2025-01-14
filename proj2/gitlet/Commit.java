package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Abdelrahman Mostafa
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** The message of this Commit. */
    private String message;
    /** the date of this commit. */
    private Date date;
    /** parent of the commit. */
    private  String parent;
    /** that contains the referencing files. */
    private  Map<String,File> trackedFiles;

    /** initial commit */
    public Commit(){
        this.message = "initial commit";
        this.date = new Date(0);
        this.parent = "";
        this.trackedFiles = new HashMap<>();

    }
    public Commit(String message,String parent ,Map<String,File> tracked){
        if(message==null){
            Repository.errorMessage("Please enter a commit message.");
        }
        this.message = message;
        this.date = new Date();
        this.parent = parent;
        this.trackedFiles = new HashMap<>(tracked);
    }
    public String saveCommit(){

            String name=Utils.sha1(this.toString());
            File f=new File(Repository.COMMIT_DIR,name);
            Utils.writeObject(f,this);
            return name;
    }
    public static Commit getCommitByName(String name){
        File f=new File(Repository.COMMIT_DIR,name);
        Commit commit=Utils.readObject(f,Commit.class);
        return commit;
    }


    /** getters */
    public String getMessage(){
        return this.message;
    }
    public Date getDate(){
        return this.date;
    }
    public String getParent(){
        return this.parent;
    }
    public Map<String, File> getTrackedFiles(){
        return this.trackedFiles;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Message: ").append(message).append("\n");
        sb.append("Date: ").append(date).append("\n");
        sb.append("Parent: ").append(parent).append("\n");
        sb.append("Tracked Files:\n");
        for (Map.Entry<String, File> entry : trackedFiles.entrySet()) {
            sb.append("  ").append(entry.getKey()).append("\n");
        }
        return sb.toString();
    }



}
