package gitlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GitFileWriter {
    
    private File _file;
    
    public GitFileWriter(String fileName) {
        _file = new File(fileName);
        try {
            _file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    void writeFile(String text) {
        try {
            FileWriter writer = new FileWriter(_file);
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    

}
