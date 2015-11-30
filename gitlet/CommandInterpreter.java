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
            InitCommand ic = new InitCommand();
            ic.execute(this);
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

}
