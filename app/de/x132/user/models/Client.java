package de.x132.user.models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import de.x132.common.CommonInformation;



@Entity
@Table(name = "Client")
@NamedQueries({
    @NamedQuery(name = Client.FIND_BY_ID, query = "SELECT client from Client client where client.id = :id"),
    @NamedQuery(name = Client.FIND_BY_NICKNAME, query = "SELECT client from Client client where client.nickname = :nickname"),
    @NamedQuery(name = Client.FIND_BY_ACTIVATION, query = "SELECT client from Client client where client.activationCode = :activationCode"),
    @NamedQuery(name = Client.FIND_BY_NICKNAME_PASSWORD, query = "SELECT client FROM Client client WHERE client.nickname = :nickname AND client.password = :password"),
    @NamedQuery(name = Client.FIND_BY_TOKEN, query = "SELECT client FROM Client client LEFT JOIN FETCH client.lastToken token WHERE token.token = :token"),
    @NamedQuery(name = Client.FIND_LIST, query = "SELECT client from Client client") })
public class Client extends CommonInformation{
    
    public static final String FIND_BY_ID = "Client.findById";
    public static final String FIND_BY_NICKNAME = "Client.findByNickname";
    public static final String FIND_BY_ACTIVATION = "Client.findActivationCode";
    public static final String FIND_BY_NAME_PASSWORD = "Client.findByNameAndPassword";
    public static final String FIND_BY_NICKNAME_PASSWORD = "Client.findByNicknameAndPassword";
    public static final String FIND_LIST = "Client.findList";
    public static final String FIND_BY_TOKEN = "Client.findByToken";
    
    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq")
    @GeneratedValue(generator = "client_gen")
    private Long id;

    @Column(name = "name", length = 32)
    private String name;

    @Column(name = "nickname", nullable = false, length = 32, unique = true)
    private String nickname;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Column(name = "password", nullable = false, length = 32)
    private String password;

    @Column(name = "host_id", nullable = false, length = 255)
    private String hostId;

    @Column(name = "surename", nullable = false, length = 32)
    private String surename;
    
    @Column(unique = true, nullable = true, length = 128)
    private String activationCode;
    
    @Enumerated
    private UserStatus status;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private List<Token> lastToken;
    

    public Client() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

    public void setSurename(String surename) {
        this.surename = surename;
    }
    
    public String getSurename(){
        return surename;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void addToken(Token token) {
        token.setCreated(new Date());
        token.setClient(this);
        token.setCreatedBy(this.getNickname());
        lastToken.add(token);
    }
}
