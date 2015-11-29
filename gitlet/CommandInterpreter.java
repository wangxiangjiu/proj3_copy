package gitlet;

import java.io.File;
import java.io.IOException;


public class CommandInterpreter {
    
    public CommandInterpreter(String[] args) {
        switch(args[0]) {
        case "init":
            initCommand();
            break;
        case "add":
            new AddCommand();
            break;
        case "commit":
            break;
        case "rm":
            break;
        case "log":
            break;
        case "global-log":
            break;
        case "find":
            break;
        case "status":
            break;
        case "checkout":
            break;
        case "branch":
            break;
        case "rm-branch":
            break;
        case "reset":
            break;
        case "merge":
            break;
        default:
                break;
            
        }
    }

    private void initCommand() {
        if (new File(".bitlet").exists()) {
            return;
        } else {
            File gitlet = new File(".gitlet");
            gitlet.mkdir();
            File objects = new File(gitlet, "objects");
            objects.mkdir();
            Commit auto = new Commit();
            File default1 = new File(objects, auto._id);
            try {
            default1.createNewFile();
            System.out.println("gitlet");
            } catch (IOException e) {
                /**Do nothing. */
            }
            
        }
        //File file = new File(".gitlet").exists();
    }

}
