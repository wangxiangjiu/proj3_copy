package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandInterpreter {

    protected List<String> _staged = new ArrayList<String>();
    protected List<String> rmStaging = new ArrayList<String>();

    public CommandInterpreter(String[] args) throws IOException, ClassNotFoundException {
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

    private void addCommand(String fileName) throws IOException, ClassNotFoundException {
        if (!new File(fileName).exists()) {
            System.err.println("File does not exist");
        } else {
            GitFileWriter gt = new GitFileWriter(fileName);
            _staged = gt.readObject();
            _staged.add(fileName);
            gt.writeObject(_staged);
        }

    }

    private void initCommand() throws IOException {
        if (new File(".gitlet").exists()) {
            return;
        } else {
            File gitlet = new File(".gitlet");
            gitlet.mkdir();
            
            File objects = new File(gitlet, "objects");
            objects.mkdir();
            
            File refs = new File(gitlet, "refs");
            refs.mkdir();
            
            File branches = new File(refs, "branches");
            branches.mkdir();
            
            Commit auto = new Commit();
            
            GitFileWriter gt = new GitFileWriter(".gitlet/objects/" + auto._id);
            GitFileWriter gt2 = new GitFileWriter(".gitlet/refs/branches/master");
            GitFileWriter gt3 = new GitFileWriter(".gitlet/objects/staging");
            GitFileWriter gt4 = new GitFileWriter(".gitlet/HEAD");
            
            gt.writeFile(auto._id);
            gt2.writeFile(auto._id);
            gt3.writeObject(_staged);
            gt4.writeFile("ref: .gitlet/refs/branches/master");

        }
    }

}
