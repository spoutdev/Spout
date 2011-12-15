package net.glowstone.temp.commons.command;

public interface CommandExecutor {
    
    /**
     * Executes the executor.  
     * 
     * @param args the command arguments for this sub-command
     * @param fullArgs all the arguments in the chat line
     * @return true if the command is thread safe
     */
    public boolean execute(String[] args, String[] fullArgs, Enum<?> command);
    
    /**
     * Indicates that this CommandExecutor is thread safe and multiple threads can call the execute() method at the same time.
     * 
     * If this method returns true, the command will be executed serially.
     * 
     * @return true if execute() is thread safe
     */
    public boolean isThreadSafe();
    
}
