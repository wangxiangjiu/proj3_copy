package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Command Interpreter for gitlet. */
public class CommandInterpreter {
    /** List of all staged files. */
    protected List<File> _staged = new ArrayList<File>();
    /** Arguments passed. */
    protected String[] _args;
    /** Is true for a dangerous command. */
    protected boolean _dangerous;

    /** Creates a command interpreter with ARGS. */
    public CommandInterpreter(String[] args) throws IOException, ClassNotFoundException {
        _args = args;
        switch (args[0]) {
        case "init":
            initCommand();
            _dangerous = false;
            break;
        case "add":
            addCommand(args[1]);
            _dangerous = false;
            break;
        case "commit":
            commitCommand(args[1]);
            _dangerous = false;
            break;
        case "rm":
            rmCommand(args[1]);
            _dangerous = true;
            break;
        case "log":
            logCommand();
            _dangerous = false;
            break;
        case "global-log":
            globLogCommand();
            _dangerous = false;
            break;
        case "find":
            _dangerous = false;
            findCommand();
            break;
        case "status":
            _dangerous = false;
            break;
        case "checkout":
            _dangerous = true;
            break;
        case "branch":
            _dangerous = false;
            break;
        case "rm-branch":
            _dangerous = false;
            break;
        case "reset":
            _dangerous = true;
            break;
        case "merge":
            _dangerous = true;
            break;
        default:
            throw new Error("unrecognizable command");
        }
    }

    private void findCommand() {
        // TODO Auto-generated method stub
        
    }

    /**Print all commits regardless of current branch. */
    private void globLogCommand() throws IOException, ClassNotFoundException {
        for(String id : GitletRepo.getAllCommitIds()){
            Commit head = GitletRepo.readCommit(id);
            System.out.println("====");
            System.out.println("Commit " + head._id + ".");     
            Date date1 = new Date(head._timeStamp);
            Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = format.format(date1);          
            System.out.println(date);
            System.out.println(head._logMessage);  
            System.out.println();
        }
    }

    /**Display information about each commit backwards 
     * along the commit tree until the initial commit.
     * @throws IOException 
     * @throws ClassNotFoundException */
    private void logCommand() throws ClassNotFoundException, IOException {
        GitletRepo gt = new GitletRepo(".gitlet/HEAD"); 
        String id = gt.getCurrentHeadPointer();
        Commit head = GitletRepo.readCommit(id);
        GitletRepo gt2;
 
        while(head._parent != null ){
            gt2 = new GitletRepo(".gitlet/objects/"+ head._id + "/" + head._id); 
            System.out.println("===");
            System.out.println("Commit " + head._id + ".");     
            Date date1 = new Date(head._timeStamp);
            Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = format.format(date1);  
            System.out.println(date);
            System.out.println(head._logMessage);  
            System.out.println();
            head = gt2.readCommit(head._parent);
        }

        gt2 = new GitletRepo(".gitlet/objects/"+ head._id + "/" + head._id); 
        System.out.println("===");
        System.out.println("Commit " + head._id + ".");     
        Date date1 = new Date(head._timeStamp);
        Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(date1);  
        System.out.println(date);
        System.out.println(head._logMessage);  
        System.out.println();
    }

    /**Untrack the file with name FILENAME. 
     * @throws IOException 
     * @throws ClassNotFoundException */
    @SuppressWarnings("unchecked")
    private void rmCommand(String fileName) throws IOException, ClassNotFoundException {
        String stagingFileName = ".gitlet/objects/staging";
        GitletRepo gt = new GitletRepo(stagingFileName);
        /**Need to change for all different branches. */ ////////////////////////////////////////////////////////////////////////////////
        GitletRepo gt2 = new GitletRepo(".gitlet/refs/branches/master");
        String currentCommitId = gt2.getCurrentHeadPointer();
        Commit currentHead = GitletRepo.readCommit(currentCommitId);
        _staged = (ArrayList<File>) gt.readObject();
//        File f1 =  new File(gt.getWorkingDirectory() + fileName);
        int i = 0;
        for (File f: _staged) {
            if (fileName.equals(f.getName())) {
                i = _staged.indexOf(f);
                f.delete();
            }
        }
        for (String a: currentHead._filePointers) {
            if (a.equals(fileName)) {
               File f2 = new File(gt.getWorkingDirectory() + "/" +  fileName);
               f2.delete();
            }
        }

        if (_staged.size() > 0) {
            _staged.remove(i);
        }
        gt.writeObject(_staged);
    }

    /** Processes a commit with given MESSAGE. */
    @SuppressWarnings("unchecked")
    private void commitCommand(String message) throws IOException, ClassNotFoundException {
        String stagingFileName = ".gitlet/objects/staging";

        /**First gitletRepo to try to read the ArrayList of Files. */
        GitletRepo gt = new GitletRepo(stagingFileName);
        ArrayList<File> _stagedFiles = (ArrayList<File>) gt.readObject();
        
        /**2nd gitletRepo to try to get the currentHead. */
        GitletRepo gt2 = new GitletRepo(".gitlet/refs/branches/master");
        String currentCommitId = gt2.getCurrentHeadPointer();
        Commit currentHead = GitletRepo.readCommit(currentCommitId);
        
        /**Create a new commit while saving the currentHead commit as the parent. */
        Commit newCommit = new Commit(System.currentTimeMillis(), message,
                currentHead._filePointers, currentCommitId);
        /**Creating the new directory inside objects directory and write the commit to it. */
        File Commit = new File(".gitlet/objects", newCommit._id);
        Commit.mkdir();
        // GitletRepo gt3 = new GitletRepo(".gitlet/objects/" + newCommit._id + "/" + newCommit._id);

        for (File file : _stagedFiles) {
            /**updating the headPointers of the newCommit. */
            newCommit._filePointers.add(file.getName());
            File newFile = new File(Commit, file.getName());
            newFile.createNewFile();
            byte[] contents = Utils.readContents(file);
            Utils.writeContents(newFile, contents);
        }
        GitletRepo.writeCommit(newCommit);

        for (File file : _stagedFiles) {
            file.delete();
        }
        _stagedFiles = new ArrayList<File>();
        gt.writeObject(_stagedFiles);
        gt2.writeFile(newCommit._id);
    }

    /** The command creates .gitlet folder. */
    private void initCommand() throws IOException {
        if (new File(".gitlet").exists()) {
            System.out.println("A gitlet version-control system already "
                    + "exists in the current directory.");
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
            Commit auto = new Commit(System.currentTimeMillis(), "initial commit", 
                    new ArrayList<String>(), null);
            File initialCommit = new File(objects, auto._id);
            initialCommit.mkdir();

           //  GitletRepo gt = new GitletRepo(".gitlet/objects/" + auto._id + "/" + auto._id);
            GitletRepo gt2 = new GitletRepo(".gitlet/refs/branches/master");
            GitletRepo gt3 = new GitletRepo(".gitlet/objects/staging");
            GitletRepo gt4 = new GitletRepo(".gitlet/HEAD");
            GitletRepo.writeCommit(auto);
            gt2.writeFile(auto._id);
            gt3.writeObject(_staged);
            gt4.writeFile("ref: .gitlet/refs/branches/master");
        }
    }

    /** Adds file with given FILENAME to staged area. */
    @SuppressWarnings("unchecked")
    private void addCommand(String fileName) throws IOException, ClassNotFoundException {
        String stagingFileName = ".gitlet/objects/staging";
        if (!new File(fileName).exists()) {
            System.err.println("File does not exist.");
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
