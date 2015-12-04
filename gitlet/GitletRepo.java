package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

/**
 * The over-arching class that has all helper methods. Enables access to all
 * Commits Staging, etc.
 */
@SuppressWarnings("serial")
public class GitletRepo implements GitletRepoHeader, Serializable {

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
    
    public void createFile(String fileName, String fileText) {
        File f = new File(fileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeFile(fileName, fileText);
    }
    
    private void writeFile(String fileName, String fileText) {
        FileWriter fw = null;
        try {
            File f = new File(fileName);
            fw = new FileWriter(f, false);
            fw.write(fileText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Writes TEXT to _file instance. */
    @Override
    public void writeFile(String text) throws IOException {
        FileWriter writer = new FileWriter(_file);
        writer.write(text);
        writer.close();
    }

    @SuppressWarnings("resource")
    public void writeCommit(Commit commit) throws IOException {
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

    /** Write the file to the outPut. */
    @SuppressWarnings("resource")
    public void writeObject(Object obj) throws IOException {
        OutputStream file = new FileOutputStream(_file);
        ObjectOutput output = new ObjectOutputStream(file);
        output.writeObject(obj);
    }

    /** Return an Object that can be casted to String, Commit etc. */
    @SuppressWarnings({ "resource" })
    public Object readObject() throws IOException, ClassNotFoundException {
        InputStream file = new FileInputStream(_file);
        ObjectInput input = new ObjectInputStream(file);
        return input.readObject();
    }
    /** Returns the commitID associated with the current commit. */
    public String getCurrentHeadPointer() throws IOException {
        String head = getText(getCurrentBranchRef());
        return head;
    }
    /** Delete branches. */
    public void deleteBranches() {
    }
    /** Creates a new directory with DIRNAME. */
    public void createDirectory(String dirName) {
        File f = new File(dirName);
        if (!f.exists()) {
            f.mkdirs();
        }
    }
    /** Gets the current Branches from. */
    public String getCurrentBranches() {
        return null;
    }
    /** Returns a STRING[] of all the branches. */
    public String[] getAllBranches() {
        return new File(".gitlet/refs/branches").list();
    }
    /** Returns the string corresponding to the current branch. */
    public String getCurrentBranchRef() throws IOException {
        String ref = getText(".gitlet/HEAD").replace("ref: ", "");
        return ref;
    }
    /** Returns STRING of actual current branch. */
    public String getCurrentBranch() throws IOException {
        return getCurrentBranchRef().replace(".gitlet/refs/heads/", "");
    }
    /** Returns a STRING of contents of FILENAME. */
    public String getText(String fileName) throws IOException {
        File file = new File(fileName);
        return new String(Utils.readContents(file));
    }
    /** Returns STRING representing the working directory. */
    public String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }
    /** Return a commit recovered from COMMITID. */
    @SuppressWarnings({ "resource", "unchecked" })
    public Commit readCommit(String commitID) throws IOException, ClassNotFoundException {
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
            String parent = (String) input.readObject();
            recovered = new Commit(timeStamp, message, filePointers, parent);
            return recovered;

        } else {
            System.out.println("Id: " + commitID + " not found!");
            return null;
        }
    }
}
