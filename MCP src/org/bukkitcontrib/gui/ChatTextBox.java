package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ChatTextBox extends GenericWidget implements Widget{
	protected int visibleLines = 10;
	protected int visibleChatLines = 20;
    public ChatTextBox() {

    }
    
    @Override
    public WidgetType getType() {
        return WidgetType.ChatTextBox;
    }
    
    @Override
    public int getNumBytes() {
        return super.getNumBytes() + 8;
    }
    
    @Override
    public void readData(DataInputStream input) throws IOException {
        super.readData(input);
        visibleLines = input.readInt();
        visibleChatLines = input.readInt();
    }

    @Override
    public void writeData(DataOutputStream output) throws IOException {
        super.writeData(output);
        output.writeInt(visibleLines);
        output.writeInt(visibleChatLines);
    }
    
    public UUID getId() {
        return new UUID(0, 3);
    }
    
    public void render() {
     
    }
    
    public int getNumVisibleLines() {
    	return visibleLines;
    }
    
    public ChatTextBox setNumVisibleLines(int lines) {
    	visibleLines = lines;
    	return this;
    }
    
    public int getNumVisibleChatLines() {
    	return visibleChatLines;
    }
    
    public ChatTextBox setNumVisibleChatLines(int lines) {
    	visibleChatLines = lines;
    	return this;
    }

}
