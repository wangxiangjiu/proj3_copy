package gitlet;

import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the gitlet package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in the gitLet package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** A dummy test to avoid complaint. */
    @Test
    public void placeholderTest() {
    }
    
    @Test
    public void testCommit() {
        Commit p1 = new Commit();
        Commit s1 = new Commit(0L, "", null, p1);
        Commit s2 = new Commit(0L, "", null, p1);
        assertEquals(false, p1._id == s1._id);
        assertEquals(false, s1._id == s2._id);
        
    }
    
    @Test
    public void testInit() {
        String[] string = new String[1];
        string[0] = "init";
        CommandInterpreter inter = new CommandInterpreter(string);
    }
}


