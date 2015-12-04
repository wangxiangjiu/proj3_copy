package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Commit implements Serializable {
    /**The SHA-1 hash value of a commit as a 40-character hexadecimal String. */
    protected String _id;
    /**The SHA-1 version of the SHA-1 hash value*/
    protected String _briefId;
    /**The timeStamp of the commit. */
    protected Long _timeStamp;
    /**The logMessage of the commit. */
    protected String _logMessage;
    /**The filePointers that is a mapping of file names to blob references. */
    protected ArrayList<String> _filePointers = new ArrayList<String>();
    /**The single parent reference. */
    protected String _parent;

    /**The constructor of commit that takes in no argument. */
    public Commit() {
        this(0L, "", null, null);
    }

    /**The constructor of commit consists of a LOGMESSAGE, TIMESTAMP, FILEPOINTERS, and PARENTSID. */
    public Commit(long timeStamp, String logMessage,
            ArrayList<String> filePointers, String parentId) {
        _timeStamp = timeStamp;
        _logMessage = logMessage;
        _filePointers = filePointers;
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
    
    /**The equals method checks if two commit OBJ is the same. */
    @Override
    public boolean equals(Object obj) {
        return false;
    }

    /**The hashCode of commit. */
    @Override
    public int hashCode() {
        return 3;
    }




}
