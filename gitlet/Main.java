package gitlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 * 
 * @author Xiangjiu Wang.
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void main(String... args) throws IOException, ClassNotFoundException {

        if (args.length == 0) {
            System.err.println("Please put arguments");
            return;
        }

        CommandInterpreter cp = new CommandInterpreter(args);
        if (cp._dangerous) {
            System.out.println("Warning: The command you entered my alter the files "
                    + "in your owrking directory. Uncomitted changes may be lost. "
                    + "Are you srue you want to continue? (yes/no)");
            Scanner stdin = new Scanner(System.in);
            String answer = stdin.nextLine();
            if(!"yes".equals(answer)) {
                cp.canExecute = false;
            return;
//            stdin.close();
            }
        }
        
//        if (cp.canExecute) {
//            command.execute();
//        }
    }

}
