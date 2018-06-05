package de.x132.comparison.service;

import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.Query;

import play.db.jpa.JPAApi;
import de.x132.common.AbstractService;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.comparison.models.Comparison;
import de.x132.comparison.transfer.ComparisonDTO;
import de.x132.user.models.Client;

/**
 * Implementierung des Services {@link ComparisonService}
 * @author Max Wick
 *
 */
public class ComparisonServiceImpl extends AbstractService<ComparisonDTO, Comparison> implements ComparisonService {

	/**
	 * Wird für die Perstierung und Zugriff von Vergleichen benötigt.
	 */
	private JPAApi api;

	/**
	 * Wird von GUICE aufgerufen und der Persistence Manager injeziert.
	 * @param api persistenz Manager.
	 */
	@Inject
	public ComparisonServiceImpl(JPAApi api) {
		super(ComparisonDTO.class);
		this.api = api;
	}

	/**
	 * @see ComparisonService#get(Client, String, String, String, String,
	 *      String)
	 */
	@Override
	public Optional<ComparisonDTO> get(Client client, String projectname, String prioritisation, String parent,
			String leftnode, String rightnode) throws NotFoundException {

		Optional<Comparison> optComparison = getComparison(client, projectname, prioritisation, parent, leftnode,
				rightnode);

		if (!optComparison.isPresent()) {
			throw new NotFoundException("Not Found");
		}
		ComparisonDTO comparisonDTO = new ComparisonDTO();
		mapToDto(optComparison.get(), comparisonDTO);

		return Optional.of(comparisonDTO);
	}

	/**
	 * Führt ein Query auf die datenbank aus um einen Vergleich zu selektieren.
	 * @param client zu dem das Projekt gehört.
	 * @param projectname zu dem die Priorisierung erstellt worden ist.
	 * @param prioritisation die Priorisierung zu der der Vergleich gehört.
	 * @param parent Elternknoten im Vergleich.
	 * @param leftnode linken Knoten im Vergleich.
	 * @param rightnode rechter Knoten im Vergleich.
	 * @return Der Vergleich aus der Datenbank.
	 */
	private Optional<Comparison> getComparison(Client client, String projectname, String prioritisation, String parent,
			String leftnode, String rightnode) {
		Query query = api.em().createNamedQuery(Comparison.FIND_COMPARISON);
		query.setParameter("nickname", client.getNickname());
		query.setParameter("project", projectname);
		query.setParameter("prioritisation", prioritisation);
		query.setParameter("parentnode", parent);
		query.setParameter("leftnode", leftnode);
		query.setParameter("rightnode", rightnode);

		Comparison comparison = (Comparison) query.getSingleResult();
		return Optional.of(comparison);
	}

	/**
	 * @see ComparisonService#update(Client, String, String, ComparisonDTO)
	 */
	@Override
	public Optional<ComparisonDTO> update(Client client, String projectname, String piorization,
			ComparisonDTO comparisonDto) throws NotFoundException {
		Optional<Comparison> optComparison = getComparison(client, projectname, piorization,
				comparisonDto.getParentNodeName(), comparisonDto.getLeftNodeName(), comparisonDto.getRightNodeName());
		if (optComparison.isPresent()) {
			Comparison comparison = optComparison.get();
			mapToEntity(comparisonDto, comparison);
			comparison.setModifiedBy(client);
			api.em().persist(comparison);
			mapToDto(comparison, comparisonDto);
			return Optional.of(comparisonDto);
		} else {
			throw new NotFoundException("Der Vergleich konnte nich in der Datenbank gefunden werden.");
		}
	}

	/**
	 * @see AbstractService#mapToEntity(de.x132.common.AbstractDTO, de.x132.common.CommonInformation)
	 */
	@Override
	public void mapToEntity(ComparisonDTO source, Comparison target) {
		if (source.getRightNodeName().equals(target.getRightnode().getName())) {
			target.setWeight(source.getWeight());
		}
	}

	/**
	 * @see AbstractService#mapToDto(de.x132.common.CommonInformation, de.x132.common.AbstractDTO)
	 */
	@Override
	public void mapToDto(Comparison source, ComparisonDTO target) {
		target.setLeftNodeName(source.getLeftnode().getName());
		target.setRightNodeName(source.getRightnode().getName());
		target.setParentNodeName(source.getParent().getName());
		target.setWeight(source.getWeight());
	}
}
