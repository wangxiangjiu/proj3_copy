package gitlet;

import java.util.ArrayList;
import java.util.List;

public class Staging {
    
    protected ArrayList<String> _filesToAdd;
    protected ArrayList<String> _filesToRm;
    
    public Staging(){
        _filesToAdd = new ArrayList<String>();
        _filesToRm = new ArrayList<String>();
    }

}
