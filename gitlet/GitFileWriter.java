package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

public class GitFileWriter {

    private File _file;

    /** The constructor of GitFileWriter creates a file named FILENAME. */
    public GitFileWriter(String fileName) throws IOException {
        _file = new File(fileName);
        _file.createNewFile();
    }

    /***/
    public void writeFile(String text) throws IOException {
        FileWriter writer = new FileWriter(_file);
        writer.write(text);
        writer.close();
    }

    /**Write the file to the outPut. */
    public void writeObject(List<String> staging) throws IOException {
        OutputStream file = new FileOutputStream(_file);
        ObjectOutput output = new ObjectOutputStream(file);
        output.writeObject(staging);
    }

    /**Return the */
    @SuppressWarnings({ "unchecked", "resource" })
    public List<String> readObject() throws IOException, ClassNotFoundException {
        InputStream file = new FileInputStream(_file);
        // System.out.println(file);
        ObjectInput input = new ObjectInputStream(file);
        return (List<String>) input.readObject();
    }

}
