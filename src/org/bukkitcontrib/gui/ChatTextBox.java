package org.bukkitcontrib.gui;

import java.util.UUID;

public class ChatTextBox extends GenericWidget implements Widget{
    public ChatTextBox() {

    }
    
    @Override
    public WidgetType getType() {
        return WidgetType.ChatTextBox;
    }
    
    public UUID getId() {
        return new UUID(0, 3);
    }
    
    public void render() {
        
    }

}
