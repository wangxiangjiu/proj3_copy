package gitlet;

import java.util.ArrayList;
import java.util.List;

public class Staging {
    
    protected List<String> _staged;
    protected List<String> _filesToRemove;
    
    public Staging(){
        _staged = new ArrayList<String>();
        _filesToRemove = new ArrayList<String>();
    }

}
