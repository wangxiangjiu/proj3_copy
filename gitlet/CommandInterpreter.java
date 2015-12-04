package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandInterpreter {

    protected List<File> _staged = new ArrayList<File>();
    protected List<String> rmStaging = new ArrayList<String>();
    protected String[] _args;
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
            commitCommand(args[1]);
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

    @SuppressWarnings("unchecked")
    private void commitCommand(String message) throws IOException, ClassNotFoundException {
        String stagingFileName = ".gitlet/objects/staging";
        GitletRepo gt = new GitletRepo(stagingFileName); 
        ArrayList<File> _stagedFiles = (ArrayList<File>) gt.readObject();
        GitletRepo gt2 = new GitletRepo(".gitlet/refs/branches/master");
        String currentCommitId = gt2.getCurrentHeadPointer();
        //System.out.println(currentCommitId);
        Commit currentHead = gt2.readCommit(currentCommitId);
        Commit newCommit = new Commit(System.currentTimeMillis(), message, currentHead._filePointers, currentCommitId);
        
        File Commit = new File(".gitlet/objects", newCommit._id);
        Commit.mkdir();
        GitletRepo gt3 = new GitletRepo(".gitlet/objects/" + newCommit._id + "/" + newCommit._id);
//        gt3.writeFile(newCommit._id);
        gt3.writeCommit(newCommit);
        // System.out.println(_stagedFiles);
        for (File file: _stagedFiles) {
            File newFile = new File(Commit, file.getName());
            System.out.println(file.getName());
            newFile.createNewFile();
           byte[] contents = Utils.readContents(file);
           Utils.writeContents(newFile, contents);
        }

        for (File file: _stagedFiles) {
           file.delete();
        }
        _stagedFiles = new ArrayList<File>();
        gt.writeObject(_stagedFiles);
        gt2.writeFile(newCommit._id);

    }


    /**The command creates .gitlet folder. */
    private void initCommand() throws IOException {
        if (new File(".gitlet").exists()) {
            System.out.println("fuck, .gitlet exists already");
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
            File stagedFiles = new File(objects, "stagedFiles");
            stagedFiles.mkdir();
            Commit auto = new Commit(System.currentTimeMillis(), "initial commit", null, null);
            File initialCommit = new File(objects, auto._id);
            initialCommit.mkdir();
            
            GitletRepo gt = new GitletRepo(".gitlet/objects/" + auto._id + "/" + auto._id);
            GitletRepo gt2 = new GitletRepo(".gitlet/refs/branches/master");
            GitletRepo gt3 = new GitletRepo(".gitlet/objects/staging");
            GitletRepo gt4 = new GitletRepo(".gitlet/HEAD");
            gt.writeCommit(auto);     
            gt2.writeFile(auto._id);
            gt3.writeObject(_staged);
            gt4.writeFile("ref: .gitlet/refs/branches/master");

        }
    }

    @SuppressWarnings("unchecked")
    private void addCommand(String fileName) throws IOException, ClassNotFoundException {
        String stagingFileName = ".gitlet/objects/staging";
        if (!new File(fileName).exists()) {
            System.err.println("File does not exist");
        } else {
            GitletRepo gt = new GitletRepo(stagingFileName);
            _staged = (ArrayList<File>) gt.readObject();
            File file = new File("./" + fileName);
            byte[] contents = Utils.readContents(file);
            File destination = new File(".gitlet/objects/stagedFiles/" + fileName);
            Utils.writeContents(destination, contents);
            _staged.add(destination);
            gt.writeObject(_staged);
        }
    }



    
    
    
    
    
}
