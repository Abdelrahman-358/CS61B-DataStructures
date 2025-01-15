package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Abdelarahman Mostafa
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        // TODO: handle errors that related to args size
        // ToDo: you must not store files at commit you must just store the name of the blobs and
        //     get them from blob file
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                Repository.add(args[1]);
                break;
            case "commit":
                Repository.commit(args[1]);
                break;
            case "rm":
                Repository.rm(args[1]);
                break;
            case "log":
                Repository.log();
                break;
//            case "global-log":
//                Repository.global_log();
        }
    }
}
