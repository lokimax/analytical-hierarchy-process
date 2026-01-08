package de.x132.ahp.service;

import de.x132.ahp.model.Client;
import de.x132.ahp.model.Project;
import de.x132.ahp.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService Tests")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Client testClient;
    private Project testProject;

    @BeforeEach
    void setUp() {
        testClient = Client.builder()
                .id(1L)
                .nickname("testuser")
                .email("test@example.com")
                .build();

        testProject = Project.builder()
                .id(1L)
                .name("Test Project")
                .beschreibung("Test Description")
                .client(testClient)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create project successfully")
    void shouldCreateProject() {
        // Given
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // When
        Project result = projectService.createProject(testProject);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project");
        assertThat(result.getClient()).isEqualTo(testClient);
        verify(projectRepository, times(1)).save(testProject);
    }

    @Test
    @DisplayName("Should find all projects by client")
    void shouldFindAllByClient() {
        // Given
        Project project2 = Project.builder()
                .id(2L)
                .name("Project 2")
                .client(testClient)
                .build();

        List<Project> expectedProjects = Arrays.asList(testProject, project2);
        when(projectRepository.findAllByClient(testClient))
                .thenReturn(expectedProjects);

        // When
        List<Project> result = projectService.findAllByClient(testClient);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testProject, project2);
        verify(projectRepository, times(1)).findAllByClient(testClient);
    }

    @Test
    @DisplayName("Should find project by id")
    void shouldFindById() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        // When
        Optional<Project> result = projectService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("Test Project");
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when project not found by id")
    void shouldReturnEmptyWhenNotFoundById() {
        // Given
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Project> result = projectService.findById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(projectRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should find project by client nickname and name")
    void shouldFindByClientNicknameAndName() {
        // Given
        when(projectRepository.findByClientNicknameAndName("testuser", "Test Project"))
                .thenReturn(Optional.of(testProject));

        // When
        Optional<Project> result = projectService.findByClientNicknameAndName("testuser", "Test Project");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Project");
        assertThat(result.get().getClient().getNickname()).isEqualTo("testuser");
        verify(projectRepository, times(1)).findByClientNicknameAndName("testuser", "Test Project");
    }

    @Test
    @DisplayName("Should check if project exists by client and name")
    void shouldCheckExistsByClientAndName() {
        // Given
        when(projectRepository.findByClientAndName(testClient, "Test Project"))
                .thenReturn(Optional.of(testProject));

        // When
        boolean exists = projectService.existsByClientAndName(testClient, "Test Project");

        // Then
        assertThat(exists).isTrue();
        verify(projectRepository, times(1)).findByClientAndName(testClient, "Test Project");
    }

    @Test
    @DisplayName("Should return false when project does not exist")
    void shouldReturnFalseWhenProjectDoesNotExist() {
        // Given
        when(projectRepository.findByClientAndName(testClient, "Nonexistent"))
                .thenReturn(Optional.empty());

        // When
        boolean exists = projectService.existsByClientAndName(testClient, "Nonexistent");

        // Then
        assertThat(exists).isFalse();
        verify(projectRepository, times(1)).findByClientAndName(testClient, "Nonexistent");
    }

    @Test
    @DisplayName("Should update project successfully")
    void shouldUpdateProject() {
        // Given
        testProject.setBeschreibung("Updated Description");
        when(projectRepository.save(testProject)).thenReturn(testProject);

        // When
        Project result = projectService.updateProject(testProject);

        // Then
        assertThat(result.getBeschreibung()).isEqualTo("Updated Description");
        verify(projectRepository, times(1)).save(testProject);
    }

    @Test
    @DisplayName("Should delete project by id")
    void shouldDeleteProject() {
        // Given
        doNothing().when(projectRepository).deleteById(1L);

        // When
        projectService.deleteProject(1L);

        // Then
        verify(projectRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should handle null client gracefully")
    void shouldHandleNullClient() {
        // Given
        when(projectRepository.findAllByClient(null))
                .thenReturn(Arrays.asList());

        // When
        List<Project> result = projectService.findAllByClient(null);

        // Then
        assertThat(result).isEmpty();
        verify(projectRepository, times(1)).findAllByClient(null);
    }
}
