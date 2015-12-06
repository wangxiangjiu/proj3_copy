package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The over-arching class that has all helper methods. Enables access to all
 * Commits Staging, etc.
 */
@SuppressWarnings("serial")
public class GitletRepo implements Serializable {

    /** File being written to or read from .*/
    private File _file;

    /** Constructor that takes in a file. */
    public GitletRepo(File file) {
        _file = file;
    }

    /** The constructor of GitletRepo creates a file named FILENAME. */
    public GitletRepo(String fileName) throws IOException {
        _file = new File(fileName);
        _file.createNewFile();
    }

    /** Writes TEXT to _file instance. */
    public void writeFile(String text) throws IOException {
        FileWriter writer = new FileWriter(_file);
        writer.write(text);
        writer.close();
    }

    /** Write the file to the outPut. */
    public void writeObject(Object obj, ObjectOutput output) throws IOException {
        output.writeObject(obj);
    }
    
    /** Creates Stream input. 
     * @throws IOException */
    public ObjectInput createInputStream() throws IOException {
        InputStream file = new FileInputStream(_file);
        ObjectInput input = new ObjectInputStream(file);
        return input;
    }
    /** Creates Stream output.
     * @throws IOException */
    public ObjectOutput createOutputStream() throws IOException {
        OutputStream file = new FileOutputStream(_file);
        ObjectOutput output = new ObjectOutputStream(file);
        return output;
    }

    /** Return an Object that can be casted to String, Commit etc. */
    public Object readObject(ObjectInput input) throws IOException, ClassNotFoundException {
        return input.readObject();
    }
//    public static void writeToStaging(List<File> files, List<String> removed, List<String> added) throws IOException {
//        String fileName = ".gitlet/objects/staging/staged";
//        //File directory = new File(directoryString);
//            OutputStream file = new FileOutputStream(fileName);
//            ObjectOutput output = new ObjectOutputStream(file);
//
//            output.writeObject(files);
//            output.writeObject(removed);
//            output.writeObject(added);
//            output.close();
//    }
//
//    @SuppressWarnings("unchecked")
//    public static  readStaging() throws IOException, ClassNotFoundException {
//        String fileName = ".gitlet/objects/staging/staged";
//
//            InputStream file = new FileInputStream(fileName);
//            ObjectInput input = new ObjectInputStream(file);
//        
//        List<List<Object>> files = new ArrayList<List<Object>>();
//        files.add((List<Object>) input.readObject());
//        files.add((List<Object>) input.readObject());
//        files.add((List<Object>) input.readObject());
//        input.close();
//        return files;
//        
//    }

    /** Returns the commitID associated with the current commit. */
    public static String getCurrentHeadPointer() throws IOException {
        String head = getText(getCurrentBranchRef());
        return head;
    }
    /** Returns the commit IDs of all commits ever made. */
    public static String[] getAllCommitIds() {
        File objects = new File(".gitlet/objects");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if("staging".equals(name) || "stagedFiles".equals(name))
                    return false;
                return true;
            }
        };
        return objects.list(filter);
    }
    /** Delete branches. */
    public void deleteBranches(String branchName) {
        File f = new File(".gitlet/refs/heads/" + branchName);
        f.delete();
    }
    /** Creates a new directory with DIRNAME. */
    public void createDirectory(String dirName) {
        File f = new File(dirName);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /** Returns a STRING[] of all the branches. */
    public static String[] getAllBranches() {
        return new File(".gitlet/refs/branches").list();
    }
    /** Returns the string corresponding to the current branch. */
    public static String getCurrentBranchRef() throws IOException {
        String ref = getText(".gitlet/HEAD").replace("ref: ", "");
        return ref;
    }
    /** Returns STRING of actual current branch. */
    public static String getCurrentBranch() throws IOException {
        return getCurrentBranchRef().replace(".gitlet/refs/branches/", "");
    }
    /** Returns a STRING of contents of FILENAME. */
    public static String getText(String fileName) throws IOException {
        File file = new File(fileName);
        return new String(Utils.readContents(file));
    }
    /** Returns STRING representing the working directory. */
    public static String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    @SuppressWarnings("resource")
    public static void writeCommit(Commit commit) throws IOException {
        String directoryString = ".gitlet/objects/" + commit._id;
        String filename = directoryString + "/" + commit._id;
        File directory = new File(directoryString);
        directory.mkdir();
            OutputStream file = new FileOutputStream(filename);
            ObjectOutput output = new ObjectOutputStream(file);

            output.writeObject(commit._id);
            output.writeObject(commit._timeStamp);
            output.writeObject(commit._logMessage);
            output.writeObject(commit._filePointers);
            output.writeObject(commit._parent);
    }
    /** Return a commit recovered from COMMITID. */
    @SuppressWarnings({ "resource", "unchecked" })
    public static Commit readCommit(String commitID) throws IOException, ClassNotFoundException {
        String objDir = ".gitlet/objects/" + commitID;
        File d = new File(objDir);

        if (!d.exists()) {
            throw new IllegalArgumentException("commit not found!");
        }

        String filename = objDir + "/" + commitID;
        Commit recovered = null;
        File f = new File(filename);
        if (f.exists()) {
            InputStream file = new FileInputStream(filename);
            ObjectInput input = new ObjectInputStream(file);

            String Id = (String) input.readObject();
            Long timeStamp = (Long) input.readObject();
            String message = (String) input.readObject();
          
            ArrayList<String> filePointers = (ArrayList<String>) input.readObject();
            //System.out.println(filePointers);
            String parent = (String) input.readObject();
            recovered = new Commit(timeStamp, message, filePointers, parent);
            return recovered;

        } else {
            System.out.println("Id: " + commitID + " not found!");
            return null;
        }
    }

//    public String getBranchHead(String branch) {
//        String path = ".gitlet/refs/heads/" + branch;
//        String head = getText(path);
//        return head;
//    }
//
//    public void makeBranchHead(String branch) {
//        String path = ".gitlet/refs/heads/" + branch;
//        if (exists(path))
//            createFile(".gitlet/HEAD", "ref: " + path);
//    }
}
