package de.x132.user.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import de.x132.common.CommonInformation;

/**
 * Token Entity zum speichern von Tokens.
 * @author Max Wick
 *
 */
@Entity
@Table(name = "token")
@NamedQueries({
        @NamedQuery(name = Token.FIND_BY_TOKEN, query = "SELECT token from Token token where token.token = :token"),
        @NamedQuery(name = Token.REMOVE_ALL_CLIENTTOKENS, query = "DELETE FROM Token token WHERE token.client = :client") })
public class Token extends CommonInformation {

    public static final String FIND_BY_TOKEN = "Token.findByToken";

    public static final String REMOVE_ALL_CLIENTTOKENS = "Token.removeAllClientTokens";

    @OneToOne
    private Client client;

    @Id
    @SequenceGenerator(name = "token_gen", sequenceName = "token_seq")
    @GeneratedValue(generator = "token_gen")
    private Long id;

    private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
