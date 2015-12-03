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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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


    @Override
    public ArrayList<Commit> getAllCommits() {
        return _allCommits;
    }

    public void deleteBranches() {

    }

    public String getCurrentBranches() {
        return null;
    }

    public void addCommitIds(Commit i) {
        _allCommits.add(i);
    }

    public String[] getAllBranches() {
        return new File(".gitlet/refs/branches").list();
    }

    public void saveCommit() {

    }

    /**Returns the commitID associated with the current commit. */
    public String getCurrentHeadPointer() throws IOException {
        String head = getText(getCurrentBranchRef());
        return head;
    }

    public String getCurrentBranchRef() throws IOException {
        String ref = getText(".gitlet/HEAD").replace("ref: ", "");
        return ref;
    }

    public String getText(String fileName) throws IOException {
        File file = new File(fileName);
        return new String(Utils.readContents(file));
    }

    public String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    public String getCurrentBranch() throws IOException {
        return getCurrentBranchRef().replace(".gitlet/refs/heads/", "");
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
}
