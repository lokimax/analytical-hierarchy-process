import com.google.inject.AbstractModule;

import de.x132.common.business.SolvingFactory;
import de.x132.common.business.SolvingFactoryImpl;
import de.x132.comparison.service.ComparisonService;
import de.x132.comparison.service.ComparisonServiceImpl;
import de.x132.connection.service.ConnectionService;
import de.x132.connection.service.ConnectionServiceImpl;
import de.x132.node.service.NodeService;
import de.x132.node.service.NodeServiceImpl;
import de.x132.prioritisation.service.PrioritisationService;
import de.x132.prioritisation.service.PrioritisationServiceImpl;
import de.x132.project.service.ProjectService;
import de.x132.project.service.ProjectServiceImpl;
import de.x132.results.service.ResultCalculationService;
import de.x132.results.service.ResultCalculationServiceImpl;
import de.x132.user.service.TokenService;
import de.x132.user.service.TokenServiceImpl;
import de.x132.user.service.UserService;
import de.x132.user.service.UserServiceImpl;

/**
 * Dies ist eine Klasse des Guice Moduls, welche dem Guice mitteilt, welche
 * unterschiedliche Service gebunden werden sollen. Das Guice Modul wird
 * aufgerufen sobald die Play! Applikation hochgefahren wird.
 *
 * Play ruft automatisch jede Klasse die von Module erbt und sich innerhalb des
 * root-Verzeichnisses befindet. Über die Konfiguration lassen sich andere
 * Module einbinden, müssen aber unter dem Parameter`play.modules.enabled` in
 * der application.conf konfiguriert werden.
 */
public class Module extends AbstractModule {

	/**
	 * Methode in der die einzelnen Service dem Interfaces gebunden werden.
	 */
	@Override
	public void configure() {
		bind(UserService.class).to(UserServiceImpl.class);
		bind(ProjectService.class).to(ProjectServiceImpl.class);
		bind(TokenService.class).to(TokenServiceImpl.class);
		bind(NodeService.class).to(NodeServiceImpl.class);
		bind(ConnectionService.class).to(ConnectionServiceImpl.class);
		bind(SolvingFactory.class).to(SolvingFactoryImpl.class);
		bind(PrioritisationService.class).to(PrioritisationServiceImpl.class);
		bind(ComparisonService.class).to(ComparisonServiceImpl.class);
		bind(ResultCalculationService.class).to(ResultCalculationServiceImpl.class);
	}

}