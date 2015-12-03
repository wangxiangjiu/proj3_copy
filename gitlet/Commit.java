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
    protected HashMap<String, String> _ref;
    protected String _parent;

    public Commit() {
        this(0L, "", null, null);
    }

    public Commit(long timeStamp, String logMessage,
            HashMap<String, String> ref, String parent) {
        _timeStamp = timeStamp;
        _logMessage = logMessage;
        _ref = ref;
        _parent = parent;

        List<Object> text = new ArrayList<Object>();

        if (parent != null) {
            text.add(parent.toString());
//            text.add(parent._ref);
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
