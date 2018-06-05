package utils.logging;

import java.util.concurrent.CompletionStage;

import play.Logger;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

/**
 * Loggt den aufrufendene Kontext.
 * @author Max Wick
 *
 */
public class LoggingAction extends Action<Logging>  {

    @Override
    public CompletionStage<Result> call(Context arg0) {
        Logger.debug(arg0.toString());
        Logger.debug(delegate.toString());
        return  delegate.call(arg0);
    }

}
