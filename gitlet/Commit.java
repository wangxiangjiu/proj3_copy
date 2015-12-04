package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Commit implements Serializable {

    protected String _id;
    protected String _briefId;

    protected Long _timeStamp;
    protected String _logMessage;
    protected HashMap<String, String> _filePointers;
    protected String _parent;

    public Commit() {
        this(0L, "", null, null);
    }

    public Commit(long timeStamp, String logMessage,
            HashMap<String, String> ref, String parentId) {
        _timeStamp = timeStamp;
        _logMessage = logMessage;
        _filePointers = ref;
        _parent = parentId;
        List<Object> text = new ArrayList<Object>();

        if (parentId != null) {
            text.add(parentId.toString());
        }
        text.add(_logMessage);
        text.add(_timeStamp.toString());
        _id = Utils.sha1(text);
        _briefId = _id.substring(0, 5);
    }
    
    @Override
    public boolean equals(Object obj) {
        return false;
    }
    
    @Override
    public int hashCode() {
        return 3;
    }




}
