package gitlet;

import java.util.ArrayList;
import java.util.List;

public class Staging {
    
    protected List<String> _staged = new ArrayList<String>();
    
    public Staging(List<String> staged){
        _staged = staged;
    }

}
