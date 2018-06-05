package de.x132.dashboard.controller;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Liefert die Startseite des Prototypen zurück, an dem der Javascript Client andocken kann.
 * @author Max Wick
 *
 */
public class StartPageController extends Controller{

	/**
	 * Stellt den index der Seite dar.
	 * @param others alles andere
	 * @return Result mit der index Page.
	 */
	public Result other(String others){
		return ok(views.html.index1.render());
	}
	
	/**
	 * Stellt den index der Seite dar.
	 * @return Result mit der index Page.
	 */
	public Result index(){
		return ok(views.html.index1.render());
	}
}
