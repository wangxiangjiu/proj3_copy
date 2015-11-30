package gitlet;

import java.io.IOException;

public interface CommandInterface {
    
    boolean dangerous();
    void execute(CommandInterpreter commandInterpreter) throws IOException;

}
