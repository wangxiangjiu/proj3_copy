package gitlet;

import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

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
        Commit s1 = new Commit(0L, "", null, "");
        Commit s2 = new Commit(0L, "", null, "");
        assertEquals(false, p1._id == s1._id);
        assertEquals(false, s1._id == s2._id);
        
    }
    
//    @Test
//    public void testInit() throws IOException, ClassNotFoundException {
//        String[] string = new String[1];
//        string[0] = "init";
//        CommandInterpreter inter = new CommandInterpreter(string);
//    }
    
    @Test
    public void testGetText() throws IOException {
        GitletRepo repo = new GitletRepo(".gitlet/HEAD");
        String a = repo.getText(".gitlet/HEAD");
//        System.out.println(a);
//        System.out.println("fuck");
//        System.out.println(repo.getCurrentHeadPointer());
    }
    
    @Test
    public void testRecoverCommit() throws IOException, ClassNotFoundException {
        Commit c1 = new Commit(0L, "fuck yeah", null, "fdsfajlsdfkjalsdkfja");
        File gitlet1 = new File(".gitlet1");
        gitlet1.mkdir();
        GitletRepo repo = new GitletRepo(".gitlet1");
        repo.writeCommit(c1);
        System.out.println(c1);
        System.out.println(repo.readCommit(c1._id));
       
        
//        assertEquals(true, c1 == repo.recoverCommit(c1._id));
    }
    
}



