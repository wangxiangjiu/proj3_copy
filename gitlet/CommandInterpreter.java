package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandInterpreter {

    protected List<String> _staged = new ArrayList<String>();
    protected List<String> rmStaging = new ArrayList<String>();
    protected String[] _args;
//    protected CommandInterface _command;
    protected boolean dangerous;

    public CommandInterpreter(String[] args) throws IOException, ClassNotFoundException {
        _args = args;
        switch (args[0]) {
        case "init":
            initCommand();
            break;
        case "add":
            addCommand(args[1]);
            break;
        case "commit":
            commitCommand();
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
            throw new Error("unrecognizable command");
        }
    }
    
    private void commitCommand() {
        
        
    }

//    CommandInterface getCommand(String commandName) {
//        return _command;
//        
//    }
    
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
            
            GitletRepo gt = new GitletRepo(".gitlet/objects/" + auto._id);
            GitletRepo gt2 = new GitletRepo(".gitlet/refs/branches/master");
            GitletRepo gt3 = new GitletRepo(".gitlet/objects/staging");
            GitletRepo gt4 = new GitletRepo(".gitlet/HEAD");
            
            gt.writeFile(auto._id);
            gt2.writeFile(auto._id);
            gt3.writeObject(_staged);
            gt4.writeFile("ref: .gitlet/refs/branches/master");

        }
    }
    
    private void addCommand(String fileName) throws IOException, ClassNotFoundException {
        String stagingFileName = ".gitlet/objects/staging";
        
        if (!new File(fileName).exists()) {
            System.err.println("File does not exist");
        } else {
            GitletRepo gt = new GitletRepo(stagingFileName); // this is just gt3. We are not creating new staging file. 
            _staged = gt.readObject();
            _staged.add(fileName);
            gt.writeObject(_staged);
        }
    }



    
    
    
    
    
}
