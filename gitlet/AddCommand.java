package gitlet;

import java.io.File;
import java.io.IOException;

public class AddCommand implements CommandInterface {
    
    protected String _fileName;
    
    public AddCommand(String fileName) {
        _fileName = fileName;  
    }
    
    @Override
    public void execute(CommandInterpreter ci) throws IOException, ClassNotFoundException {
        if (!new File(_fileName).exists()) {
            System.err.println("File does not exist");
        } else {
            GitFileWriter gt = new GitFileWriter(_fileName);
            ci._staged = gt.readObject();
            ci._staged.add(_fileName);
            gt.writeObject(ci._staged);
        }
    }

    @Override
    public boolean isDangerous() {
        return false;
    }
    

}
