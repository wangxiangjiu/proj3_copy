package gitlet;

import java.util.ArrayList;
import java.util.List;

public class Staging {
    
    protected List<String> _staged;
    protected List<String> _filesToRemove;
    
    public Staging(ArrayList<String> staged){
        _staged = staged;
    }

}
