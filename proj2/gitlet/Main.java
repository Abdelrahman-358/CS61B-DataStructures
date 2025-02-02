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
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                //initializing gitlet repo
                Repository.init();
                break;
            case "add":
                //add file
                if(args.length == 2)
                Repository.add(args[1]);
                else Repository.errorMessage("not enough arguments");
                break;
            case "commit":
                if(args.length == 2)
                Repository.commit(args[1]);
                else Repository.errorMessage("Please enter a commit message.");
                break;
            case "rm":
                if(args.length == 2){
                    //remove file
                    Repository.rm(args[1]);
                }
                else {
                    //remove branch
                    Repository.rmBranch(args[3]);
                }
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                Repository.global_log();
                break;
            case "find":
                Repository.find(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                if(args[1].equals("--")){
                    Repository.checkout(args[2]);
                }else if(args.length>2){
                    Repository.checkout(args[1],args[3]);
                }else {
                    Repository.checkoutBranch(args[1]);
                }
                break;
            case "branch":
                Repository.branch(args[1]);
                break;
            case "reset":
                if(args.length == 2)
                Repository.reset(args[1]);
                else Repository.errorMessage("not enough arguments");
                break;
            case "merge":
                Repository.merge(args[1]);
                break;


        }
    }
}
