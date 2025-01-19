package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Abdelrahman Mostafa
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** The message of this Commit. */
    private final String message;
    /** the date of this commit. */
    private final Date date;
    /** parent of the commit. */
    private final String parent;
    /** that contains the referencing files. */
    // Map Store Name -> sha
    private final Map<String,String> trackByName;
    // Map Store sha -> Name
    private final Map<String,String> trackBySha;

    /** initial commit */
    public Commit(){
        this.message = "initial commit";
        this.date = new Date(0);
        this.parent = "stop";
        this.trackByName = new HashMap<>();
        this.trackBySha = new HashMap<>();

    }
    public Commit(String message,String parent ,Map<String,String> tracked){
        if(message==null){
            Repository.errorMessage("Please enter a commit message.");
        }
        this.message = message;
        this.date = new Date();
        this.parent = parent;
        // tacked sha -> file
        this.trackBySha = new HashMap<>();
        this.trackByName = new HashMap<>();
        for(Map.Entry<String,String> entry: tracked.entrySet()){
            trackByName.put(entry.getKey(),entry.getValue());
            trackBySha.put(entry.getValue(),entry.getKey());
        }
    }
    public String saveCommit(){

            String name=Utils.sha1(this.toString());
            File f=new File(Repository.COMMIT_DIR,name);
            Utils.writeObject(f,this);
            return name;
    }
    /**
     *return commit object using commit name
     * */
    public static Commit getCommitByName(String name){
        File f=new File(Repository.COMMIT_DIR,name);
        Commit commit=Utils.readObject(f,Commit.class);
        return commit;
    }


    public  String getTrackedFileByName(String name){
        return this.trackByName.get(name);
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
    public Map<String, String> getTrackByName(){
        return this.trackByName;
    }
    public Map<String, String> getTrackBySha(){
        return this.trackBySha;
    }
    public static boolean commitExists(String commitName){
        File f=new File(Repository.COMMIT_DIR,commitName);
        return f.exists();
    }
    public boolean isFileTracked(String fileName){
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
