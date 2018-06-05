package de.x132.node.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import play.db.jpa.JPAApi;
import de.x132.common.AbstractService;
import de.x132.common.exceptionhandling.ConflictException;
import de.x132.common.exceptionhandling.InternalServerException;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.node.models.Node;
import de.x132.node.transfer.NodeDTO;
import de.x132.project.models.Project;
import de.x132.user.models.Client;

/**
 * Service zum Erstellen, Lesen, Aktualisieren und Löschen von Node Objekten.
 * 
 * @author Max Wick
 *
 */
public class NodeServiceImpl extends AbstractService<NodeDTO, Node>implements NodeService {

    private JPAApi jpa;

    @Inject
    public NodeServiceImpl(JPAApi jpa) {
    	super(NodeDTO.class);
        this.jpa = jpa;
    }

    /**
     * @see NodeServiceImpl#list(Project, int, int)
     */
    @Override
    public List<NodeDTO> list(Project project, int page, int count) throws InternalServerException {
        TypedQuery<Node> query = jpa.em().createNamedQuery(Node.FIND_ALL_NODES_FOR_PROJECT, Node.class);
        query.setParameter("nickname", project.getClient().getNickname());
        query.setParameter("projectname", project.getName());
        query.setFirstResult((page - 1) * count);
        query.setMaxResults(count);
        List<Node> nodes = query.getResultList();
        try {
			List<NodeDTO> mapList = mapList(nodes);
	        return mapList;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new InternalServerException(e);
		}
    }
    
    /**
     * @see NodeServiceImpl#delete(Project, String)
     */
	@Override
	public Optional<NodeDTO> delete(Project project, String nodename) {
        Optional<Node> optNode = this.getNode(project.getClient(), project.getName(), nodename);
        if(optNode.isPresent()){
        	Node node = optNode.get();
        	jpa.em().remove(node);
            NodeDTO returnDto = new NodeDTO();
            mapToDto(node, returnDto);
            return Optional.of(returnDto);
        }
        return Optional.empty();
	}

    /**
     * @see NodeServiceImpl#create(Project, NodeDTO)
     */
    @Override
    public Optional<NodeDTO> create(Project project, NodeDTO nodeDto) throws ConflictException {
        Node node = new Node();
        node.setModifiedBy(project.getClient());
        mapToEntity(nodeDto, node);
        node.setProject(project);
        try{
        	jpa.em().persist(node);
        } catch(Exception ex){
        	throw new ConflictException("Node " + nodeDto.getName() + " still exists in this Project.");
        }
        NodeDTO returnDto = new NodeDTO();
        mapToDto(node, returnDto);
        return Optional.of(returnDto);
    }

    /**
     * @see NodeServiceImpl#getNode(Project, String)
     */
    @Override
    public Optional<NodeDTO> getNode(Project project, String nodename) throws NotFoundException {
    	Node node = this.get(project, nodename).orElseThrow(() -> new NotFoundException("Cannot find node for " + project + " project and " + nodename + "node"));
       	NodeDTO nodeDto = new NodeDTO();
       	mapToDto(node, nodeDto);
        return Optional.of(nodeDto);
    }

    /**
     * @see NodeServiceImpl#update(Project, String, NodeDTO)
     */
    @Override
    public Optional<NodeDTO> update(Project project, String nodename, NodeDTO nodeDto) {
    	Optional<Node> optNode = this.get(project, nodename);
        if(optNode.isPresent()){
        	Node node = optNode.get();
        	node.setModifiedBy(project.getClient());
        	mapToEntity(nodeDto, node);
        	jpa.em().persist(node);
        	mapToDto(node, nodeDto);
        }
        return Optional.of(nodeDto);
    }

    /**
     * @see NodeServiceImpl#get(Project, String)
     */
	@Override
	public Optional<Node> get(Project project, String nodename) {
        Query query = jpa.em().createNamedQuery(Node.FIND_NODE_BY_NAME);
        query.setParameter("nickname", project.getClient().getNickname());
        query.setParameter("projectname", project.getName());
        query.setParameter("nodename", nodename);
        Node node = (Node) query.getSingleResult();
        return Optional.of(node);
	}

    /**
     * @see NodeServiceImpl#getNode(Client, String, String)
     */
	@Override
	public Optional<Node> getNode(Client client, String projectname, String nodename) {
		TypedQuery<Node> query = jpa.em().createNamedQuery(Node.FIND_NODE_BY_NAME, Node.class);
		query.setParameter("nickname", client.getNickname());
		query.setParameter("projectname", projectname);
		query.setParameter("nodename", nodename);
		Node node = query.getSingleResult();
		return Optional.of(node);
	}

    /**
     * @see NodeServiceImpl#getStartNodes(Client, String)
     */
	@Override
	public List<Node> getStartNodes(Client client, String projectname) throws InternalServerException {
		TypedQuery<Node> query = jpa.em().createNamedQuery(Node.FIND_STARTNODES, Node.class);
		query.setParameter("nickname", client.getNickname());
		query.setParameter("projectname", projectname);
		return query.getResultList();
	}
	
	/**
	 * @see NodeService#getEndNodes(Client, String)
	 */
	@Override
	public List<Node> getEndNodes(Client client, String projectname) throws InternalServerException {
		TypedQuery<Node> query = jpa.em().createNamedQuery(Node.FIND_ENDNODES, Node.class);
		query.setParameter("nickname", client.getNickname());
		query.setParameter("projectname", projectname);
		return query.getResultList();
	}

    /**
     * @see AbstractService#mapToEntity(de.x132.common.AbstractDTO, de.x132.common.CommonInformation)
     */
	@Override
	public void mapToEntity(NodeDTO source, Node target) {
		target.setBeschreibung(source.getBeschreibung());
		target.setContent(source.getContent());
		target.setName(source.getName());
	}

    /**
     * @see AbstractService#mapToDto(de.x132.common.CommonInformation, de.x132.common.AbstractDTO)
     */
	@Override
	public void mapToDto(Node source, NodeDTO target) {
		target.setBeschreibung(source.getBeschreibung());
		target.setContent(source.getContent());
		target.setName(source.getName());
	}

}
