package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Command Interpreter for gitlet. */
public class CommandInterpreter {
    /** List of all staged files. */
    protected List<File> _staged = new ArrayList<File>();
    /***/
    protected List<String> _addedFileNames = new ArrayList<String>();
    /***/
    protected List<String> _removedFileNames = new ArrayList<String>();
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
            findCommand(args[1]);
            break;
        case "status":
            statusCommand();
            _dangerous = false;
            break;
        case "checkout":
            switch (args.length) {
            case 3:
                checkOutfileName(args[2]);
                break;
            case 4:
                checkOutIdFileName(args[1], args[3]);
                break;
            case 2:
                checkOutBranch(args[1]);
            }
            _dangerous = true;
            break;
        case "branch":
            branchCommand(args[1]);
            _dangerous = false;
            break;
        case "rm-branch":
            // rmBranch();
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

    private void checkOutIdFileName(String commitID, String fileName) {
        String directory = ".gitlet/objects/" + commitID;
        File commitDirectory = new File(directory);
        if (!commitDirectory.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        File commitFile = new File(commitDirectory, fileName);
        if (!commitFile.exists()) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String workingDirectory = GitletRepo.getWorkingDirectory();
        File file = new File(workingDirectory + "/" + fileName);
        byte[] contents = Utils.readContents(commitFile);
        Utils.writeContents(file, contents);
    }
    /***/
    private void checkOutBranch(String branchName) throws IOException, ClassNotFoundException {
        if () {
            System.out.println("There is an untracked file in the way; delete it or add it first.");
        }
        if (GitletRepo.getCurrentBranch().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
        }
        String path = ".gitlet/HEAD";
        String contents = GitletRepo.getText(path).replace(GitletRepo.getCurrentBranch(),
                branchName);

        File file = new File(path);
        Utils.writeContents(file, contents.getBytes());
        String currentCommitID = null;
        try {
        currentCommitID = GitletRepo.getCurrentHeadPointer();
        } catch (IllegalArgumentException e) {
            System.out.println("No such branch exists.");
            return;
        }
        Commit currentCommit = GitletRepo.readCommit(currentCommitID);
        for (String fileName : currentCommit._filePointers) {
            File commitFile = new File(".gitlet/objects/" + currentCommitID + "/" + fileName);
            String workingDirectory = GitletRepo.getWorkingDirectory();
            File newfile = new File(workingDirectory + "/" + fileName);
            Utils.writeContents(newfile, Utils.readContents(commitFile));
        }
    }

    /***/
    private void checkOutfileName(String fileName) throws IOException {
        try {
            String currentCommitID = GitletRepo.getCurrentHeadPointer();
            File commitFile = new File(".gitlet/objects/" + currentCommitID + "/" + fileName);
            String workingDirectory = GitletRepo.getWorkingDirectory();
            File file = new File(workingDirectory + "/" + fileName);
            Utils.writeContents(file, Utils.readContents(commitFile));
        } catch (IllegalArgumentException e) {
            System.out.println("File does not exist in that commit.");
        }

    }

    @SuppressWarnings("unchecked")
    private void statusCommand() throws IOException, ClassNotFoundException {
        GitletRepo gt = new GitletRepo(".gitlet/objects/staging");
        ObjectInput input = gt.createInputStream();
        _staged = (List<File>) gt.readObject(input);
        _removedFileNames = (List<String>) gt.readObject(input);
        _addedFileNames = (List<String>) gt.readObject(input);

        System.out.println("=== Branches ===");
        String[] Branches = GitletRepo.getAllBranches();
        Arrays.sort(Branches);
        for (String branch : Branches) {
            if (branch.equals(GitletRepo.getCurrentBranch())) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        Collections.sort(_addedFileNames);
        for (String fileName : _addedFileNames) {
            System.out.println(fileName);
        }

        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String fileName : _removedFileNames) {
            System.out.println(fileName);
        }
        System.out.println();
        System.out.println("=== Modification Not Staged For Commit ===");

        System.out.println();
        System.out.println("=== Untracted Files ===");
        ObjectOutput output = gt.createOutputStream();
        gt.writeObject(_staged, output);
        gt.writeObject(_removedFileNames, output);
        gt.writeObject(_addedFileNames, output);
    }

    /**
     * @throws IOException
     */
    private void branchCommand(String branchName) throws IOException {
        String directory = ".gitlet/refs/branches/";
        GitletRepo gt = new GitletRepo(directory + branchName);
        gt.writeFile(GitletRepo.getCurrentHeadPointer());
    }

    /**
     * Prints out the id associated with the commit by passing in COMMITMESSAGE.
     */
    private void findCommand(String commitMessage) throws ClassNotFoundException, IOException {
        String[] ids = GitletRepo.getAllCommitIds();
        int count = 0;
        for (String id : ids) {
            if (GitletRepo.readCommit(id)._logMessage.equals(commitMessage)) {
                System.out.println(id);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("no commit with that message.");
        }
    }

    /** Print all commits regardless of current branch. */
    private void globLogCommand() throws IOException, ClassNotFoundException {
        for (String id : GitletRepo.getAllCommitIds()) {
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

    /**
     * Display information about each commit backwards along the commit tree
     * until the initial commit.
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void logCommand() throws ClassNotFoundException, IOException {
        String id = GitletRepo.getCurrentHeadPointer();
        Commit head = GitletRepo.readCommit(id);

        while (head._parent != null) {
            System.out.println("===");
            System.out.println("Commit " + head._id + ".");
            Date date1 = new Date(head._timeStamp);
            Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = format.format(date1);
            System.out.println(date);
            System.out.println(head._logMessage);
            System.out.println();
            head = GitletRepo.readCommit(head._parent);
        }

        System.out.println("===");
        System.out.println("Commit " + head._id + ".");
        Date date1 = new Date(head._timeStamp);
        Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(date1);
        System.out.println(date);
        System.out.println(head._logMessage);
        System.out.println();
    }

    /**
     * Untrack the file with name FILENAME.
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private void rmCommand(String fileName) throws IOException, ClassNotFoundException {
        String stagingFileName = ".gitlet/objects/staging";
        GitletRepo gt = new GitletRepo(stagingFileName);
        /** Need to change for all different branches. */ ////////////////////////////////////////////////////////////////////////////////
        // GitletRepo gt2 = new GitletRepo(GitletRepo.getCurrentBranchRef());
        String currentCommitId = GitletRepo.getCurrentHeadPointer();
        Commit currentHead = GitletRepo.readCommit(currentCommitId);
        ObjectInput input = gt.createInputStream();
        _staged = (ArrayList<File>) gt.readObject(input);
        _removedFileNames = (ArrayList<String>) gt.readObject(input);
        _addedFileNames = (ArrayList<String>) gt.readObject(input);
        // File f1 = new File(gt.getWorkingDirectory() + fileName);
        int i = 0;
        for (File f : _staged) {
            if (fileName.equals(f.getName())) {
                i = _staged.indexOf(f);
                f.delete();
            }
        }
        String removedFileName = null;
        for (String a : currentHead._filePointers) {
            if (a.equals(fileName)) {
                removedFileName = a;
                _removedFileNames.add(removedFileName);
                File f2 = new File(gt.getWorkingDirectory() + "/" + fileName);
                f2.delete();
            }
        }
        // File removedFile = null;
        //
        if (_staged.size() > 0) {
            _staged.remove(i);
        }

        ObjectOutput output = gt.createOutputStream();
        gt.writeObject(_staged, output);
        gt.writeObject(_removedFileNames, output);
        gt.writeObject(_addedFileNames, output);

    }

    /** Processes a commit with given MESSAGE. */
    @SuppressWarnings("unchecked")
    private void commitCommand(String message) throws IOException, ClassNotFoundException {
        String stagingFileName = ".gitlet/objects/staging";

        /** First gitletRepo to try to read the ArrayList of Files. */
        GitletRepo gt = new GitletRepo(stagingFileName);
        ObjectInput input = gt.createInputStream();
        ArrayList<File> stagedFiles = (ArrayList<File>) gt.readObject(input);
        ArrayList<String> removedFileNames = (ArrayList<String>) gt.readObject(input);
        ArrayList<String> addedFileNames = (ArrayList<String>) gt.readObject(input);

        /** 2nd gitletRepo to try to get the currentHead. */
        GitletRepo gt2 = new GitletRepo(".gitlet/refs/branches/master");
        String currentCommitId = GitletRepo.getCurrentHeadPointer();
        Commit currentHead = GitletRepo.readCommit(currentCommitId);

        /**
         * Create a new commit while saving the currentHead commit as the
         * parent.
         */
        Commit newCommit = new Commit(System.currentTimeMillis(), message,
                currentHead._filePointers, currentCommitId);
        /**
         * Creating the new directory inside objects directory and write the
         * commit to it.
         */
        File Commit = new File(".gitlet/objects", newCommit._id);
        Commit.mkdir();
        // GitletRepo gt3 = new GitletRepo(".gitlet/objects/" + newCommit._id +
        // "/" + newCommit._id);

        for (File file : stagedFiles) {
            /** updating the headPointers of the newCommit. */
            newCommit._filePointers.add(file.getName());
            File newFile = new File(Commit, file.getName());
            newFile.createNewFile();
            byte[] contents = Utils.readContents(file);
            Utils.writeContents(newFile, contents);
        }
        GitletRepo.writeCommit(newCommit);

        for (File file : stagedFiles) {
            file.delete();
        }
        stagedFiles = new ArrayList<File>();
        removedFileNames = new ArrayList<String>();
        addedFileNames = new ArrayList<String>();
        ObjectOutput output = gt.createOutputStream();
        gt.writeObject(stagedFiles, output);
        gt.writeObject(removedFileNames, output);
        gt.writeObject(addedFileNames, output);
        gt2.writeFile(newCommit._id);

        GitletRepo gt3 = new GitletRepo(GitletRepo.getCurrentBranchRef());
        gt3.writeFile(newCommit._id);
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

            // GitletRepo gt = new GitletRepo(".gitlet/objects/" + auto._id +
            // "/" + auto._id);
            GitletRepo gt2 = new GitletRepo(".gitlet/refs/branches/master");
            GitletRepo gt3 = new GitletRepo(".gitlet/objects/staging");
            GitletRepo gt4 = new GitletRepo(".gitlet/HEAD");
            GitletRepo.writeCommit(auto);
            gt2.writeFile(auto._id);
            ObjectOutput output = gt3.createOutputStream();
            gt3.writeObject(_staged, output);
            gt3.writeObject(_removedFileNames, output);
            gt3.writeObject(_addedFileNames, output);
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
            ObjectInput input = gt.createInputStream();
            _staged = (ArrayList<File>) gt.readObject(input);
            _removedFileNames = (ArrayList<String>) gt.readObject(input);
            _addedFileNames = (ArrayList<String>) gt.readObject(input);
            File file = new File("./" + fileName);
            byte[] contents = Utils.readContents(file);
            File destination = new File(".gitlet/objects/stagedFiles/" + fileName);
            Utils.writeContents(destination, contents);
            String fileString = destination.getName();
            _addedFileNames.add(fileString);
            _staged.add(destination);
            // _addedFilesName.add(destination.getName());
            ObjectOutput output = gt.createOutputStream();
            gt.writeObject(_staged, output);
            gt.writeObject(_removedFileNames, output);
            gt.writeObject(_addedFileNames, output);
        }
    }
}
