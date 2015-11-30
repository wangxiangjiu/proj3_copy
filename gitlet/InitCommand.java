package gitlet;

import java.io.File;
import java.io.IOException;

public class InitCommand implements CommandInterface {

    public void execute(CommandInterpreter CI) throws IOException {
        if (new File(".gitlet").exists()) {
            return;
        } else {
            File gitlet = new File(".gitlet");
            gitlet.mkdir();

            File objects = new File(gitlet, "objects");
            objects.mkdir();

            File refs = new File(gitlet, "refs");
            refs.mkdir();

            File branches = new File(refs, "branches");
            branches.mkdir();

            Commit auto = new Commit();

            GitFileWriter gt = new GitFileWriter(".gitlet/objects/" + auto._id);
            GitFileWriter gt2 = new GitFileWriter(".gitlet/refs/branches/master");
            GitFileWriter gt3 = new GitFileWriter(".gitlet/objects/staging");
            GitFileWriter gt4 = new GitFileWriter(".gitlet/HEAD");

            gt.writeFile(auto._id);
            gt2.writeFile(auto._id);
            gt3.writeObject(CI._staged);
            gt4.writeFile("ref: .gitlet/refs/branches/master");
        }
    }

    @Override
    public boolean isDangerous() {
        return false;
    }
}
