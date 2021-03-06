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
import java.util.HashMap;

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

    protected boolean canExecute;

    /** Creates a command interpreter with ARGS. */
    public CommandInterpreter(String[] args) throws IOException, ClassNotFoundException {
        _args = args;
        switch (args[0]) {
        case "init":
            _dangerous = false;
            initCommand();
            break;
        case "add":
            _dangerous = false;
            addCommand(args[1]);
            break;
        case "commit":
            _dangerous = false;
            try {
                commitCommand(args[1]);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Please enter a commit message.");
                return;
            }
            break;
        case "rm":
            _dangerous = true;
            rmCommand(args[1]);

            break;
        case "log":
            _dangerous = false;
            logCommand();
            break;
        case "global-log":
            _dangerous = false;
            globLogCommand();
            break;
        case "find":
            _dangerous = false;
            findCommand(args[1]);
            break;
        case "status":
            _dangerous = false;
            statusCommand();
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
            _dangerous = false;
            branchCommand(args[1]);
            break;
        case "rm-branch":
            _dangerous = false;
            rmBranch(args[1]);
            break;
        case "reset":
            _dangerous = true;
            reset(args[1]);
            break;
        case "merge":
            _dangerous = true;
            merge(args[1]);
            break;
        default:
            throw new Error("unrecognizable command");
        }
    }

    /**
     * Merges files from the given BRANCHNAME into the current branch.
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private void merge(String branchName) throws IOException, ClassNotFoundException {
        if (!_addedFileNames.isEmpty() || !_removedFileNames.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        if (!Arrays.asList(GitletRepo.getAllBranches()).contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (GitletRepo.getCurrentBranch().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        String currentCommitID = GitletRepo.getCurrentHeadPointer();
        Commit currentCommit = GitletRepo.readCommit(currentCommitID);
        List<String> list = GitletRepo.unTracked(currentCommit);
        if (list.size() > 0) {
            System.out.println("There is an untracked file in the way; delete it or add it first.");
            return;
        }
        try {
            Commit current = GitletRepo.readCommit(GitletRepo.getCurrentHeadPointer());
            GitletRepo.getBranchHead(branchName);
            Commit other = GitletRepo.readCommit(GitletRepo.getBranchHead(branchName));
            Commit split = GitletRepo.readCommit(current.findSplitPoint(other));
            System.out.println(split._id);

            HashMap<String, String> currentMod = new HashMap<String, String>();
            HashMap<String, String> otherMod = new HashMap<String, String>();
            HashMap<String, String> currentNotMod = new HashMap<String, String>();
            HashMap<String, String> otherNotMod = new HashMap<String, String>();
            HashMap<String, String> currentNew = new HashMap<String, String>();
            HashMap<String, String> otherNew = new HashMap<String, String>();

            ArrayList<String> currentFP = current._filePointers;
            ArrayList<String> otherFP = other._filePointers;
            ArrayList<String> splitFP = split._filePointers;

            if (split._id.equals(other._id)) {
                System.out.println("Given branch is a ancestor of the current branch.");
                return;
            }
            if (split._id.equals(current._id)) {
                for (String fileName : other._filePointers) {
                    File file = new File(GitletRepo.getWorkingDirectory() + "/" + fileName);
                    String id = getIDFromFileName(fileName, other);
                    File file2 = new File(".gitlet/objects/" + id + "/" + fileName);
                    byte[] contents = Utils.readContents(file2);
                    Utils.writeContents(file, contents);
                }
                String commitID = GitletRepo.getBranchHead(branchName);
                String path = ".gitlet/refs/branches" + GitletRepo.getCurrentBranch();
                File file = new File(path);
                Utils.writeContents(file, commitID.getBytes());
                System.out.println("Current branch fast-forwarded.");
                return;
            }
            
            for (String fileName : currentFP) {
                String fileCommit = getIDFromFileName(fileName, current);
                if (!split._filePointers.contains(fileName)) {
                    currentNew.put(fileName, fileCommit);
                } else {
                    String splitCommit = getIDFromFileName(fileName, split);

                    if (!fileCommit.equals(splitCommit)) {
                        currentMod.put(fileName, fileCommit);
                    } else {
                        currentNotMod.put(fileName, fileCommit);
                    }
                }
            }

            for (String fileName : otherFP) {
                String fileCommit = getIDFromFileName(fileName, other);
                if (!split._filePointers.contains(fileName)) {
                    otherNew.put(fileName, fileCommit);
                } else {
                    String splitCommit = getIDFromFileName(fileName, split);

                    if (!fileCommit.equals(splitCommit)) {
                        otherMod.put(fileName, fileCommit);
                    } else {
                        otherNotMod.put(fileName, fileCommit);
                    }
                }
            }

            GitletRepo gt = new GitletRepo(".gitlet/objects/staging");
            
//            System.out.println("fileNames in other:" + otherMod.keySet());
//            System.out.println("fileNames in current:" + currentMod.keySet());
//            System.out.println("fileNames not in other:" + otherNotMod.keySet());
//            System.out.println("fileNames not in current:" + currentNotMod.keySet());

            for (String fileName : otherMod.keySet()) {
                if (currentNotMod.keySet().contains(fileName)) {
                    String path1 = ".gitlet/objects/" + currentNotMod.get(fileName) + "/"
                            + fileName;
                    String path2 = ".gitlet/objects/" + otherMod.get(fileName) + "/" + fileName;
                    byte[] currFContents = Utils.readContents(path1);
                    byte[] othFContents = Utils.readContents(path2);
                    if (!Arrays.equals(currFContents, othFContents)) {
                        String workingDirePath = GitletRepo.getWorkingDirectory() + "/" + fileName;
                        File file2 = new File(workingDirePath);
                        _staged.add(file2);
                        _addedFileNames.add(fileName);
                        Utils.writeContents(file2, othFContents);

                        File file = new File(GitletRepo.getWorkingDirectory() + "/" + fileName);
                        byte[] contents = Utils.readContents(file);
                        File destination = new File(".gitlet/objects/stagedFiles/" + fileName);
                        Utils.writeContents(destination, contents);
                    }
                }
            }
            
            /** adding new files present in the given branch to the working directory and add them to staging. */
            for (String fileName: otherNew.keySet()) {
                File file = new File(GitletRepo.getWorkingDirectory() + "/" + fileName);
                String commitPath = ".gitlet/objects/" + otherNew.get(fileName) + "/" + fileName;
                File fileOrigin = new File(commitPath);
                byte[] contents = Utils.readContents(fileOrigin);
                Utils.writeContents(file, contents);

                _staged.add(file);
                _addedFileNames.add(fileName);
                
                File destination = new File(".gitlet/objects/stagedFiles/" + fileName);
                Utils.writeContents(destination, contents);
            }
            
            /** remove files present in split, modified in current and absent in given branch. */
            for (String fileName: currentMod.keySet()) {
                if (!other._filePointers.contains(fileName)) {
                    File fileToDelete = new File(".gitlet/objects/" + currentMod.get(fileName) + "/" + fileName);
                    fileToDelete.delete();
                    File file = new File(GitletRepo.getWorkingDirectory() + "/" + fileName);
                    file.delete();
                    current._filePointers.remove(fileName);
                }
            }
            
            
            ObjectOutput oo = gt.createOutputStream();
            gt.writeObject(_staged, oo);
            gt.writeObject(_removedFileNames, oo);
            gt.writeObject(_addedFileNames, oo);
        } catch (Error e) {
            return;
        }
    }

    public static String getIDFromFileName(String fileName, Commit commit)
            throws ClassNotFoundException, IOException {
        File commitFile = new File(".gitlet/objects/" + commit._id + "/" + fileName);
        while (!commitFile.exists()) {
            commit = GitletRepo.readCommit(commit._parent);
            commitFile = new File(".gitlet/objects/" + commit._id + "/" + fileName);
        }
        return commit._id;
    }

    /**
     * Resets to COMMITSTRING and moves head to that commit.
     */
    private void reset(String commitID) throws IOException, ClassNotFoundException {
        String directory = ".gitlet/objects/" + commitID;
        File commitDirectory = new File(directory);
        if (!commitDirectory.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }

        String currentCommitID = GitletRepo.getCurrentHeadPointer();
        Commit currentCommit = GitletRepo.readCommit(currentCommitID);
        
        List<String> untrackedStrings = GitletRepo.unTracked(currentCommit);
        if (untrackedStrings.size() > 0) {
            System.out.println("There is an untracked file in the way; delete it or add it first.");
            return;
        }

        String branch = ".gitlet/refs/branches/" + GitletRepo.getCurrentBranch();
        File branchFile = new File(branch);

        Utils.writeContents(branchFile, commitID.getBytes());
        
        currentCommit = GitletRepo.readCommit(commitID);
        System.out.println(currentCommit._id + "commit files: " + currentCommit._filePointers);
        for (String fileName : currentCommit._filePointers) {
            Commit iter = currentCommit;
            File commitFile = new File(".gitlet/objects/" + iter._id + "/" + fileName);

            while (!commitFile.exists()) {
                iter = GitletRepo.readCommit(iter._parent);
                commitFile = new File(".gitlet/objects/" + iter._id + "/" + fileName);
            }

            String workingDirectory = GitletRepo.getWorkingDirectory();
            File newfile = new File(workingDirectory + "/" + fileName);
            Utils.readContents(commitFile);
            Utils.writeContents(newfile, Utils.readContents(commitFile));
        }

        untrackedStrings = GitletRepo.unTracked(currentCommit);
        for (String fileString : untrackedStrings) {
            File fileD = new File(GitletRepo.getWorkingDirectory() + "/" + fileString);
            fileD.delete();
        }
    }

    /***/
    private void rmBranch(String branchName) throws IOException {
        if (branchName.equals(GitletRepo.getCurrentBranch())) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        if (!Arrays.asList(GitletRepo.getAllBranches()).contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        String path = ".gitlet/refs/branches/" + branchName;
        File file = new File(path);
        file.delete();
    }

    /***/
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
        if (GitletRepo.getCurrentBranch().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        if (!Arrays.asList(GitletRepo.getAllBranches()).contains(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }

        String currentCommitID = GitletRepo.getCurrentHeadPointer();
        Commit currentCommit = GitletRepo.readCommit(currentCommitID);
//        System.out.println("current commit: " + currentCommitID);
        List<String> untrackedStrings = GitletRepo.unTracked(currentCommit);
        if (untrackedStrings.size() > 0) {
            System.out.println("There is an untracked file in the way; delete it or add it first.");
            return;
        }

        String path = ".gitlet/HEAD";
        String currentBranch = GitletRepo.getCurrentBranch();
        String contents = GitletRepo.getText(path).replace(currentBranch, branchName);
        File file = new File(path);
        Utils.writeContents(file, contents.getBytes());

        currentCommitID = GitletRepo.getCurrentHeadPointer();
        currentCommit = GitletRepo.readCommit(currentCommitID);
        for (String fileName : currentCommit._filePointers) {
            Commit iter = currentCommit;
            File commitFile = new File(".gitlet/objects/" + iter._id + "/" + fileName);

            while (!commitFile.exists()) {
                iter = GitletRepo.readCommit(iter._parent);
                commitFile = new File(".gitlet/objects/" + iter._id + "/" + fileName);
            }

            String workingDirectory = GitletRepo.getWorkingDirectory();
            File newfile = new File(workingDirectory + "/" + fileName);
            Utils.readContents(commitFile);
            Utils.writeContents(newfile, Utils.readContents(commitFile));
        }

        untrackedStrings = GitletRepo.unTracked(currentCommit);
        for (String fileString : untrackedStrings) {
            File fileD = new File(GitletRepo.getWorkingDirectory() + "/" + fileString);
            fileD.delete();
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
            System.out.println("Found no commit with that message.");
        }
    }

    /** Print all commits regardless of current branch. */
    private void globLogCommand() throws IOException, ClassNotFoundException {
        for (String id : GitletRepo.getAllCommitIds()) {
            Commit head = GitletRepo.readCommit(id);
            System.out.println("====");
            System.out.println("Commit " + head._id);
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
            System.out.println("Commit " + head._id);
            Date date1 = new Date(head._timeStamp);
            Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = format.format(date1);
            System.out.println(date);
            System.out.println(head._logMessage);
            System.out.println();
            head = GitletRepo.readCommit(head._parent);
        }

        System.out.println("===");
        System.out.println("Commit " + head._id);
        Date date1 = new Date(head._timeStamp);
        Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(date1);
        System.out.println(date);
        System.out.println(head._logMessage);
        System.out.println();
    }
    
//    public String[] allParentsOfBranch(String branchName) {
//        String headCommitID = GitletRepo.getBranchHead(branchName);
//        Commit headCommit = GitletRepo.readCommit(headCommitID);
//        while (headCommit._parent != null) {
//            System.out.println();
//        }
//        
//    }

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

        /** Need to change for all different branches. */
        String currentCommitId = GitletRepo.getCurrentHeadPointer();
        Commit currentHead = GitletRepo.readCommit(currentCommitId);
        ObjectInput input = gt.createInputStream();
        _staged = (ArrayList<File>) gt.readObject(input);
        _removedFileNames = (ArrayList<String>) gt.readObject(input);
        _addedFileNames = (ArrayList<String>) gt.readObject(input);

        if (!_staged.contains(fileName) && !currentHead._filePointers.contains(fileName)) {
            System.out.println("No reason to remove the file.");
            return;
        }
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
                File f2 = new File(GitletRepo.getWorkingDirectory() + "/" + fileName);
//                File f3 = new File(".gitlet/objects/" + currentHead._id + "/" + fileName);
//                f3.delete();
                f2.delete();
            }
        }


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

        if (addedFileNames.isEmpty() && removedFileNames.isEmpty()) {
            System.out.println("No changes added to the commit.");
        }

        /** 2nd gitletRepo to try to get the currentHead. */
        GitletRepo gt2 = new GitletRepo(".gitlet/refs/branches/" + GitletRepo.getCurrentBranch());
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
            System.out.println("commmited file: " + file.getName());
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
            File file = new File(GitletRepo.getWorkingDirectory() + "/" + fileName);
            byte[] contents = Utils.readContents(file);
            File destination = new File(".gitlet/objects/stagedFiles/" + fileName);
            Utils.writeContents(destination, contents);
            String fileString = destination.getName();
            _addedFileNames.add(fileString);
            _staged.add(destination);

            ObjectOutput output = gt.createOutputStream();
            gt.writeObject(_staged, output);
            gt.writeObject(_removedFileNames, output);
            gt.writeObject(_addedFileNames, output);
        }
    }
}
