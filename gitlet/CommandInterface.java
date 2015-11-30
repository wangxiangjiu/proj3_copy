package gitlet;

import java.io.IOException;

public interface CommandInterface {
    
      boolean isDangerous();
      void execute(CommandInterpreter commandInterpreter) throws IOException, ClassNotFoundException;

}
