package gitlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandInterpreter {

    protected List<String> _staged = new ArrayList<String>();
    protected List<String> rmStaging = new ArrayList<String>();
    protected String[] _args;
    protected CommandInterface _command;

    public CommandInterpreter(String[] args) throws IOException, ClassNotFoundException {
        _args = args;
    }
    
    boolean statement(String[] args) throws IOException, ClassNotFoundException {
        switch (args[0]) {
        case "init":
            _command = new InitCommand();
            _command.execute(this);
            break;
        case "add":
            AddCommand add = new AddCommand(args[1]);
            add.execute(this);
            break;
        case "commit":
            CommitCommand cm = new CommitCommand();
            cm.execute();
            break;
        case "rm":
            break;
        case "log":
            break;
        case "global-log":
            break;
        case "find":
            break;
        case "status":
            break;
        case "checkout":
            break;
        case "branch":
            break;
        case "rm-branch":
            break;
        case "reset":
            break;
        case "merge":
            break;
        default:
            throw new Error("unrecognizable command");
        }
        return true;
    }
    
    CommandInterface getCommand(String commandName) {
        return _command;
        
    }

    
    
    
    
    
}
