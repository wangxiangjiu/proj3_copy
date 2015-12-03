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
import java.util.List;

/**
 * The over-arching class that has all helper methods. Enables access to all
 * Commits Staging, etc.
 */
@SuppressWarnings("serial")
public class GitletRepo implements GitletRepoHeader, Serializable {

    private File _file;
    protected ArrayList<Commit> _allCommits;
    protected Commit _currCommit;
    protected ArrayList<String> _staging;

    /** The constructor of GitletRepo creates a file named FILENAME. */
    public GitletRepo(String fileName) throws IOException {
        _file = new File(fileName);
        _file.createNewFile();
    }

    /***/
    @Override
    public void writeFile(String text) throws IOException {
        FileWriter writer = new FileWriter(_file);
        writer.write(text);
        writer.close();
    }

    /** Write the file to the outPut. */
    @SuppressWarnings("resource")
    public void writeObject(List<String> staging) throws IOException {
        OutputStream file = new FileOutputStream(_file);
        ObjectOutput output = new ObjectOutputStream(file);
        output.writeObject(staging);
    }

    /** Return */
    @SuppressWarnings({ "unchecked", "resource" })
    public ArrayList<String> readObject() throws IOException, ClassNotFoundException {
        InputStream file = new FileInputStream(_file);
        ObjectInput input = new ObjectInputStream(file);
        return (ArrayList<String>) input.readObject();
    }

    @Override
    public ArrayList<Commit> getAllCommits() {
        return _allCommits;
    }

    @Override
    public String getCurrentCommitId() {
        return null;
    }

    public void deleteBranches() {

    }

    public String getCurrentBranches() {
        return null;
    }

    public void addCommitIds(Commit i) {
        _allCommits.add(i);
    }

    public void saveToStaging() {

    }

    public void recoverToStaging() {

    }

    /**Return a commit recovered from COMMITID. */
    @SuppressWarnings({ "resource", "unchecked" })
    public Commit recoverCommit(String commitID) throws IOException, ClassNotFoundException {
        String objDir = ".gitlet/objects/" + commitID;
        File d = new File(objDir);

        if (!d.exists()) {
            throw new IllegalArgumentException("commit not found!");
        }

        String filename = objDir + "/" + commitID;
        Commit recovered = null;
        Long timeStamp;
        String message;
        HashMap<String, String> filePointers;
        File f = new File(filename);
        if (f.exists()) {
            InputStream file = new FileInputStream(filename);
            ObjectInput input = new ObjectInputStream(file);
            /**how does readObject works. */
            String parentId = (String) input.readObject();
            message = (String) input.readObject();
            timeStamp = (Long) input.readObject();
            filePointers = (HashMap<String, String>) input.readObject();
            recovered = new Commit(timeStamp, message, filePointers, parentId);
            return recovered;

        } else {
            System.out.println("Id: " + commitID + " not found!");
            return null;
        }
    }

    public String[] getAllBranches() {
        return new File(".gitlet/refs/branches").list();
    }

    public void saveCommit() {

    }

}
