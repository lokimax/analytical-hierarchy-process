package de.x132.user.transfer;

import de.x132.common.AbstractDTO;

/**
 * DatatTransfer Object für einen Benutzer.
 * 
 * @author Max Wick
 *
 */
public class UserDTO extends AbstractDTO{

    private String nickname;
    
    private String email;
    
    private String password;
    
    private String hostId;
    
    private String name;
    
    private String surename;

    public UserDTO(String name, String surename) {
        this.setName(name);
        this.setSurename(surename);
    }

    public UserDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurename() {
        return surename;
    }

    public void setSurename(String surename) {
        this.surename = surename;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }
}
