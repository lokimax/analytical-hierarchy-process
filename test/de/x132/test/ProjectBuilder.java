package de.x132.test;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.mockito.internal.util.reflection.Whitebox;

import de.x132.connection.models.Connection;
import de.x132.node.models.Node;
import de.x132.project.models.Project;

public class ProjectBuilder {

	private String projectName;
	private HashMap<String, Node> nodes;
	private HashMap<String, Connection> connections;

	public ProjectBuilder(String projectName) {
		nodes = new HashMap<>();
		connections = new HashMap<>();
		this.projectName = projectName;
	}

	public Project build() {
		Project project = new Project();
		project.setName(projectName);
		for(Node node : this.nodes.values()){
			List<Connection> outgoing = connections.entrySet()
			.stream().filter(x -> x.getKey().startsWith(node.getName()+":"))
			.map(x -> { return x.getValue();}).collect(Collectors.toList());

			List<Connection> incoming = connections.entrySet()
			.stream().filter(x -> x.getKey().endsWith(":" + node.getName()))
			.map(x -> { return x.getValue();}).collect(Collectors.toList());
			
			node.setIngoing(incoming);
			node.setOutgoing(outgoing);
		}
		Whitebox.setInternalState(project, "nodes", nodes.values().stream().collect(Collectors.toList()));
		Whitebox.setInternalState(project, "connections", connections.values().stream().collect(Collectors.toList()));
		return project;
	}

	public ProjectBuilder addNode(String nodeName) {
		Node node = new Node();
		node.setName(nodeName);
		this.nodes.put(nodeName, node);
		return this;
	}
	
	public ProjectBuilder addConnection(String nodename1, String nodename2){
		Node node = nodes.get(nodename1);
		Node node2 = nodes.get(nodename2);
		String key = nodename1+":"+nodename2;
		Connection connection = new Connection();
		connection.setSourcenode(node);
		connection.setTargetnode(node2);
		connections.put(key, connection);
		return this;
	}

	

}
