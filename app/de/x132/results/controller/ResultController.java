package de.x132.results.controller;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.math3.exception.NoDataException;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.StatusHeader;
import utils.logging.Logging;
import de.x132.common.ErrorResponseUtils;
import de.x132.common.ErrorType;
import de.x132.common.business.transfer.ChildResultDTO;
import de.x132.common.business.transfer.FullResultDTO;
import de.x132.common.business.transfer.SolvingResultDTO;
import de.x132.common.exceptionhandling.BadRequestException;
import de.x132.common.exceptionhandling.InternalServerException;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.common.exceptionhandling.NotImplementedException;
import de.x132.common.exceptionhandling.UnauthorizedException;
import de.x132.results.service.ResultCalculationService;
import de.x132.user.actions.Authenticated;
import de.x132.user.models.Client;
import de.x132.user.service.UserService;

/**
 * Controller um Results zu erstellen. Intern wird der ResultService aufgerufen,
 * innerhalb des Controllers werden die DTO in JSOn umgesetzt und Fehler auf
 * HTTP Fehler übersetzt.
 * 
 * @author Max Wick
 *
 */
public class ResultController extends Controller {

	private UserService userservice;
	private ResultCalculationService resultCalculationService;

	@Inject
	public ResultController(UserService userservice, ResultCalculationService resultCalculationService) {
		this.userservice = userservice;
		this.resultCalculationService = resultCalculationService;
	}

	/**
	 * Liefert Result für nur einen Knoten.
	 * 
	 * @param projectname
	 *            Name des PRojektes
	 * @param prioritisation
	 *            Name der Priorisierung
	 * @param parent
	 *            Elternknoten für den die Ergebnisse geliefert werden sollen.
	 * @return Result zu einem Knoten.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result getResultsForNode(String projectname, String prioritisation, String parent) {
		try {
			Client client = this.userservice.getClient(request()).orElseThrow(
					() -> new UnauthorizedException("Not Authrorized"));
			SolvingResultDTO localResults = this.resultCalculationService.getLocalResults(client, projectname,
					prioritisation, parent).orElseThrow(() -> new InternalServerException("Cannopt generate Result"));
			return ok(Json.toJson(localResults));
		} catch (NoDataException ex) {
			return noContent();
		} catch (UnauthorizedException ex) {
			return unauthorized();
		} catch (InternalServerException ex) {
			return internalServerError(ex.getMessage());
		} catch (NotFoundException e) {
			return notFound(e.getMessage());
		} catch (BadRequestException e) {
			return badRequest(e.getMessage());
		}
	}

	/**
	 * Liefert die vollständige Lösung des Netzwerkes. Jeder Knotenelement, der
	 * innerhalb des Netzwerkes vorkommt wird hier ausgegegeben.
	 * 
	 * @param projectname
	 *            Name des PRojektes über den der Result erzeugt werden soll.
	 * @param prioritisation
	 *            Name der Priorirsierung die für die Ergebniserstellung
	 *            herangezogen werden soll.
	 * @return Ergebnis in Form von JSON.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result getFullResults(String projectname, String prioritisation) {
		try {
			Client client = this.userservice.getClient(request()).orElseThrow(
					() -> new UnauthorizedException("Not Authrorized"));
			FullResultDTO localResults = this.resultCalculationService.getFullResult(client, projectname,
					prioritisation).orElseThrow(() -> new InternalServerException("Cannot generate Result"));
			return ok(Json.toJson(localResults));
		} catch (NoDataException ex) {
			return noContent();
		} catch (UnauthorizedException ex) {
			return unauthorized();
		} catch (InternalServerException ex) {
			return internalServerError(ex.getMessage());
		} catch (NotFoundException e) {
			return notFound(e.getMessage());
		} catch (BadRequestException e) {
			return badRequest(e.getMessage());
		}
	}

	/**
	 * Liefert Auswirkungen eines Knotens auf die Blattelemente.
	 * 
	 * @param projectname
	 *            Name des Projektes.
	 * @param prioritisation
	 *            Name der Priorisierung
	 * @return ein Fullresult nur mit den Kindelementen.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result getResultsForLeafs(String projectname, String prioritisation) {
		try {
			Client client = this.userservice.getClient(request()).orElseThrow(
					() -> new UnauthorizedException("Not Authrorized"));
			FullResultDTO localResults = this.resultCalculationService.getChildResult(client, projectname,
					prioritisation).orElseThrow(() -> new InternalServerException("Cannopt generate Result"));
			return ok(Json.toJson(localResults));
		} catch (NoDataException ex) {
			return noContent();
		} catch (UnauthorizedException ex) {
			return unauthorized();
		} catch (InternalServerException ex) {
			return internalServerError(ex.getMessage());
		} catch (NotFoundException e) {
			return notFound(e.getMessage());
		} catch (BadRequestException e) {
			return badRequest(e.getMessage());
		}
	}

	/**
	 * Liefert die Einflussgrößen der <code>Parents</code> für einen
	 * <code>Child</code>-Knoten.
	 * 
	 * @param projectname
	 *            des aktuellen Projektes.
	 * @param prioritisation
	 *            die innerhalb der Berechnung betrachtet werden soll.
	 * @param child
	 *            Kind knoten für den die Priorisierung erstellt werden soll.
	 * @return eine Liste der Einflussgrößen.
	 */
	@Logging
	@Authenticated
	@Transactional(readOnly = true)
	public Result getInfluenceResults(String projectname, String prioritisation, String child) {
		try {
			Client client = this.userservice.getClient(request()).orElseThrow(
					() -> new UnauthorizedException("Not Authorized"));
			SolvingResultDTO localResults = this.resultCalculationService.getInfluenceResults(client, projectname,
					prioritisation, child).orElseThrow(() -> new InternalServerException("Cannot generate Result"));
			return ok(Json.toJson(localResults));
		} catch (NoDataException ex) {
			return noContent();
		} catch (UnauthorizedException ex) {
			return unauthorized(ErrorResponseUtils.createError(ErrorType.INFO, "Unauthorized User"));
		} catch (InternalServerException e) {
			return internalServerError(ErrorResponseUtils.createError(ErrorType.CRITICAL, e.getMessage()));
		} catch (NotFoundException e) {
			return notFound(ErrorResponseUtils.createError(ErrorType.INFO, e.getMessage()));
		} catch (BadRequestException e) {
			return badRequest(ErrorResponseUtils.createError(ErrorType.WARNING, e.getMessage()));
		} catch (NotImplementedException e) {
			return status(NOT_IMPLEMENTED,
					ErrorResponseUtils.createError(ErrorType.WARNING, "Methode is not implemented yet"));
		}
	}

	/**
	 * Liefert
	 * @param projectname
	 * @param prioritisation
	 * @param parentname
	 * @return
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result getResultsForChilds(String projectname, String prioritisation, String parentname) {
		try {
			Client client = this.userservice.getClient(request()).orElseThrow(
					() -> new UnauthorizedException("Not Authorized"));
			ChildResultDTO result = this.resultCalculationService.getResultsForChilds(client, projectname,
					prioritisation, parentname)
					.orElseThrow(() -> new InternalServerException("Cannot generate Result"));
			return ok(Json.toJson(result));
		} catch (UnauthorizedException ex) {
			return unauthorized(ErrorResponseUtils.createError(ErrorType.INFO, "Unauthorized User"));
		} catch (InternalServerException e) {
			return internalServerError(ErrorResponseUtils.createError(ErrorType.CRITICAL, e.getMessage()));
		} catch (NotFoundException e) {
			return notFound(ErrorResponseUtils.createError(ErrorType.INFO, e.getMessage()));
		} catch (BadRequestException e) {
			return badRequest(ErrorResponseUtils.createError(ErrorType.WARNING, e.getMessage()));
		} catch (NotImplementedException e) {
			return status(NOT_IMPLEMENTED,
					ErrorResponseUtils.createError(ErrorType.WARNING, "Methode is not implemented yet"));
		}

	}
}
