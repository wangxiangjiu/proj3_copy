package gitlet;

import java.util.ArrayList;
import java.util.List;

import Commands.*;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Xiangjiu Wang.
 */
public class Main {
    
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        
        if (args.length == 0) {
            System.err.println("Please put arguments");
            return;
        }
        
        CommandInterpreter cp = new CommandInterpreter(args);
        
        
        
        
    }

    

}
