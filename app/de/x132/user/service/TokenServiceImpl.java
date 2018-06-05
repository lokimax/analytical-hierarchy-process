package de.x132.user.service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.codec.binary.Hex;

import de.x132.common.exceptionhandling.ForbiddenException;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.user.models.Client;
import de.x132.user.models.Token;
import de.x132.user.models.UserStatus;
import play.Configuration;
import play.Logger;
import play.db.jpa.JPAApi;
import play.mvc.Http.Request;

/**
 * Implementierung des Interfaces {@link TokenService}
 * @author Max Wick
 *
 */
public class TokenServiceImpl implements TokenService {

    private JPAApi jpaApi;
    private Configuration configuration;

    @Inject
    public TokenServiceImpl(JPAApi jpaApi, Configuration configuration ) {
        this.jpaApi = jpaApi;
        this.configuration = configuration;
    }

    /**
     * @see TokenService#isValid(Request)
     */
    @Override
    public Optional<Boolean> isValid(Request request) {
        String header = request.getHeader("authorization");
        
        Optional<String> strToken = determineToken(header);
        try {
           if(strToken.isPresent()){
               Token token = findToken(strToken.get());

               Long now = new Date().getTime();
               Long tokenGeneration = token.getCreated().getTime();

               if ((now - tokenGeneration) > (configuration.getLong("token.experation") * 1000)) {
                   Logger.debug("Token " + token + " expired.");
                   Logger.debug("Token generated: " + token.getCreated());
                   Logger.debug("Now: " + new Date());
                   return Optional.of(Boolean.FALSE);
               }
               Logger.debug(token.getToken() + " is valid.");
               return Optional.of(Boolean.TRUE);
           }
           Logger.info(strToken + " is not present");
           return Optional.of(Boolean.FALSE);
        } catch (NoResultException nre) {
            Logger.info("No Result found for " + strToken);
            return Optional.of(Boolean.FALSE);
        }
    }

	private Optional<String> determineToken(String header) {
		if(header != null){
			if(header.startsWith("Basic")){
				return Optional.of(header.substring(6, header.length()));
			} else if(header.startsWith("Bearer")){
				return Optional.of(header.substring(7, header.length()));
			}
		}
		return Optional.empty();
	}

    /**
     * Sucht den Token in der Datenbank
     * @param strToken der in der Datenbank gesucht werden soll.
     * @return Tokenelement wenn in der Datenbank sich einer befunden hat.
     */
    private Token findToken(final String strToken) {
        Token token =  jpaApi.withTransaction(() -> {
                EntityManager em = jpaApi.em();
                Query query = em.createNamedQuery(Token.FIND_BY_TOKEN);
                query.setParameter("token", strToken);
                Object singleResult = query.getSingleResult();
                return (Token) singleResult;
            });
        return token;
    }
    
    /**
     * @see TokenService#getToken(Request)
     */
    @Override
    public Optional<String> getToken(Request request) throws NotFoundException, ForbiddenException {
        String header = request.getHeader("authorization");
        Logger.info("authorization:" + header);

        Optional<String> userNameAndPassword = determineToken(header);
        if(userNameAndPassword.isPresent()){
        	byte[] bytes;
            try {
                bytes = userNameAndPassword.get().getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                return Optional.empty();
            }
            Decoder decoder = Base64.getDecoder();
            byte[] decodeBase64 = decoder.decode(bytes);

            String userAndPass = new String(decodeBase64);       
            String[] split = userAndPass.split(":");
            Logger.info("nickname:" + split[0]);
            Logger.info("password:" + split[1]);
            String username = split[0];
            String password = split[1];
            Client client = this.findByNicknameAndPassword(username, password).orElseThrow(() -> new NotFoundException("Client not Found"));
            
            if (!client.getStatus().equals(UserStatus.ACTIVE)) {
                throw new ForbiddenException("User is not active.");
            }
            
            String encodeHexString = addNewTokenTo(client);
          
            return Optional.of(encodeHexString);
        } else {
        	throw new NotFoundException("Client not found.");
        }
    }

    /**
     * Erstellt einen neuen token zum Client.
     * @param client zu dem der Token erzeugt werden soll.
     * @return Neuer Token.
     */
    private String addNewTokenTo(Client client) {
        SecureRandom random = new SecureRandom();
        byte secure[] = new byte[8];
        random.nextBytes(secure);
        // String strToken = bytes.toString();
        String encodeHexString = Hex.encodeHexString(secure);
        Token token = new Token();
        token.setLastmodified(new Date());
        token.setToken(encodeHexString);
        client.addToken(token);
        return encodeHexString;
    }
    
    /**
     * @see TokenService#deleteTokens(Request)
     */
    @Override
    public void deleteTokens(Request request) throws NotFoundException {
        String header = request.getHeader("authorization");
        Optional<String> strToken = determineToken(header);
        if(strToken.isPresent()){
	        Client client = findByToken(strToken.get()).orElseThrow(() -> new NotFoundException("Client not found."));
	        Optional<Integer> deleteTokensFor = deleteTokensFor(client);
	        Logger.info(deleteTokensFor.get() + " deleted for " + client.getNickname());
        } else {
        	throw new NotFoundException("Client not found.");
        }
    }
    
    /**
     * Löscht einen Token aus der Datenbank
     * @param client dem der Token gehört.
     * @return Optional mit der Anzahl der gelöschten Tokens.
     */
    private Optional<Integer> deleteTokensFor(Client client) {
        Query createNamedQuery = jpaApi.em().createNamedQuery(Token.REMOVE_ALL_CLIENTTOKENS);
        createNamedQuery.setParameter("client", client);
        int executeUpdate = createNamedQuery.executeUpdate();
        return Optional.of(Integer.valueOf(executeUpdate));   
    }

    /**
     * Sucht nach einem Benutzer anhand des Benutzernamens und passwortes.
     * @param username Benutzername nach dem gesucht werden soll.
     * @param password nachdem gesucht werden soll.
     * @return das entsprechende Client object.
     */
	private Optional<Client> findByNicknameAndPassword(String username, String password) {
		Query createNamedQuery = jpaApi.em().createNamedQuery(Client.FIND_BY_NICKNAME_PASSWORD);
		createNamedQuery.setParameter("nickname", username);
		createNamedQuery.setParameter("password", password);

		try {
			return Optional.of((Client) createNamedQuery.getSingleResult());
		} catch (NoResultException nre) {
			return Optional.empty();
		}
	}
    
	/**
	 * Findet den passenden Client zum Token.
	 * @param token zu dem der passende Benutzer gefunden werden soll.
	 * @return Optional wenn der Benutzer erfolgreich zum token gefudnen werden konnte.
	 */
    private Optional<Client> findByToken(String token){
        Query createNamedQuery = jpaApi.em().createNamedQuery(Client.FIND_BY_TOKEN);
        createNamedQuery.setParameter("token", token);
        return Optional.of((Client)createNamedQuery.getSingleResult());
    }

}
