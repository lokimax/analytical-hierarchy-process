package de.x132.user.actions;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Results;
import de.x132.user.service.TokenService;

/**
 * Authenticated Action überprüft, ob der Benutzer einen validen Tokenb im Header gesendet hat.
 * Diese Action wird anhand der Annotation zwischen dem Router und Controller-Action aufgerufen.
 * @author Max Wick
 *
 */
public class AuthenticationAction extends Action.Simple {

	/**
	 * wird zum verifizieren des Tokens gebraucht.
	 */
    private final TokenService tokenservice;
    
    /**
     * Standard Konstruktorzur injezierung des Tokenservices.
     * @param tokenservice
     */
    @Inject
    public AuthenticationAction(TokenService tokenservice){
        this.tokenservice = tokenservice;
    }
    
    /**
     * Diese Methoden wird zwischen Router und der tatsächlichen Controller Action aufgerufen.
     * Sie überprüft anhand des Tokenservices, ob der Benutzer valide Cridentials übergeben hat.
     */
    @Override
    public CompletionStage<Result> call(Context context) {
        
        Optional<Boolean> optional = tokenservice.isValid(context.request());
        
        if(optional.isPresent()){
            if(Boolean.TRUE.equals(optional.get())){
                return delegate.call(context);
            } else {
                return CompletableFuture.completedFuture(
                        Results.forbidden()
                );    
            }
        } else {
            return CompletableFuture.completedFuture(
                    Results.internalServerError("Internal Server Error cannot validate")
            );
        }
    }
    
}
