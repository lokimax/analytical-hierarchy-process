package de.x132.ahp.service;

import de.x132.ahp.model.Client;
import de.x132.ahp.model.Project;
import de.x132.ahp.repository.ProjectRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProjectService {

  private final ProjectRepository projectRepository;

  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public Project createProject(Project project) {
    return projectRepository.save(project);
  }

  public Optional<Project> findById(Long id) {
    return projectRepository.findById(id);
  }

  public List<Project> findAllByClient(Client client) {
    return projectRepository.findAllByClient(client);
  }

  public Optional<Project> findByClientAndName(Client client, String name) {
    return projectRepository.findByClientAndName(client, name);
  }

  public Optional<Project> findByClientNicknameAndName(String nickname, String projectName) {
    return projectRepository.findByClientNicknameAndName(nickname, projectName);
  }

  public Project updateProject(Project project) {
    return projectRepository.save(project);
  }

  public void deleteProject(Long id) {
    projectRepository.deleteById(id);
  }

  public List<Project> findAll() {
    return projectRepository.findAll();
  }

  public boolean existsByClientAndName(Client client, String name) {
    return projectRepository.findByClientAndName(client, name).isPresent();
  }
}
