package de.x132.node.transfer;

import de.x132.common.AbstractDTO;

public class NodeDTO extends AbstractDTO {

    private String name;
    
    private String content;
    
    private String beschreibung;

    public NodeDTO(){
        
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }
    
}
