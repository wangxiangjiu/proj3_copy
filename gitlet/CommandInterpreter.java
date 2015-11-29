package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandInterpreter {

    protected List<String> addStaging = new ArrayList<String>();
    protected List<String> rmStaging = new ArrayList<String>();

    public CommandInterpreter(String[] args) {
        switch (args[0]) {
        case "init":
            initCommand();
            break;
        case "add":
            // AddCommand add = new AddCommand(args[1]);
            // add.execute();
            addCommand(args[1]);
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

    private void addCommand(String fileName) {
        if (!new File(fileName).exists()) {
            System.err.println("File does not exist");
        } else {
            addStaging.add(fileName);
        }

    }

    private void initCommand() {
        if (new File(".gitlet").exists()) {
            return;
        } else {
            Commit auto = new Commit();
            GitFileWriter gt = new GitFileWriter(".gitlet/objects/" + auto._id);
//            GitFileWriter gt2 = new GitFileWriter(".gitlet/refs/Head/master");
//            File gitlet = new File(".gitlet");
//            gitlet.mkdir();
//            File objects = new File(gitlet, "objects");
//            objects.mkdir();
//            Commit auto = new Commit();
//            File default1 = new File(objects, auto._id);
//            try {
//                default1.createNewFile();
//                System.out.println("gitlet");
//            } catch (IOException e) {
//                /** Do nothing. */
//            }

        }
        // File file = new File(".gitlet").exists();
    }

}
