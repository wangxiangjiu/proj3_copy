package gitlet;

import java.io.IOException;
import java.util.ArrayList;

public interface GitletRepoHeader {
    
    public void writeFile(String text) throws IOException;
    public ArrayList<Commit> getAllCommits();
}
