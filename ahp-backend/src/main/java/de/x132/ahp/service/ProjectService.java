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

  /**
   * Check if a user owns a project.
   *
   * @param projectId the project ID
   * @param user the user to check
   * @return true if the user owns the project, false otherwise
   */
  public boolean isOwner(Long projectId, Client user) {
    return projectRepository
        .findById(projectId)
        .map(project -> project.getClient().equals(user))
        .orElse(false);
  }

  /**
   * Get a project if the user owns it.
   *
   * @param projectId the project ID
   * @param user the user who should own the project
   * @return an Optional containing the project if the user owns it, empty otherwise
   */
  public Optional<Project> getByIdAndOwner(Long projectId, Client user) {
    return projectRepository
        .findById(projectId)
        .filter(project -> project.getClient().equals(user));
  }

  /**
   * Get all projects owned by a specific user.
   *
   * @param user the user whose projects to retrieve
   * @return a list of all projects owned by the user
   */
  public List<Project> getAllByOwner(Client user) {
    return projectRepository.findAllByClient(user);
  }
}
