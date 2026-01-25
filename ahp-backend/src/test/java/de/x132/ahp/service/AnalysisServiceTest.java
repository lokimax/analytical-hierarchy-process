package de.x132.ahp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.Project;
import de.x132.ahp.model.json.AnalysisResult;
import de.x132.ahp.repository.AnalysisRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalysisService Tests")
class AnalysisServiceTest {

  @Mock private AnalysisRepository analysisRepository;

  @InjectMocks private AnalysisService analysisService;

  private Project testProject;
  private Analysis testAnalysis;

  @BeforeEach
  void setUp() {
    testProject =
        Project.builder().id(1L).name("Test Project").beschreibung("Test Description").build();

    testAnalysis =
        Analysis.builder()
            .id(1L)
            .name("Test Analysis")
            .beschreibung("Test Analysis Description")
            .project(testProject)
            .criteriaComparisons("{}")
            .alternativeComparisons("{}")
            .results(AnalysisResult.builder().build())
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("Should create analysis successfully")
  void shouldCreateAnalysis() {
    // Given
    when(analysisRepository.save(any(Analysis.class))).thenReturn(testAnalysis);

    // When
    Analysis result = analysisService.createAnalysis(testAnalysis);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Test Analysis");
    verify(analysisRepository, times(1)).save(testAnalysis);
  }

  @Test
  @DisplayName("Should find all analyses by project ordered by created date desc")
  void shouldFindAllByProjectOrderedByDate() {
    // Given
    Analysis analysis2 =
        Analysis.builder()
            .id(2L)
            .name("Analysis 2")
            .project(testProject)
            .createdAt(LocalDateTime.now().plusDays(1))
            .build();

    List<Analysis> expectedAnalyses = Arrays.asList(analysis2, testAnalysis);
    when(analysisRepository.findByProjectOrderByCreatedAtDesc(testProject))
        .thenReturn(expectedAnalyses);

    // When
    List<Analysis> result = analysisService.findAllByProject(testProject);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("Analysis 2");
    assertThat(result.get(1).getName()).isEqualTo("Test Analysis");
    verify(analysisRepository, times(1)).findByProjectOrderByCreatedAtDesc(testProject);
  }

  @Test
  @DisplayName("Should find analysis by project and name")
  void shouldFindByProjectAndName() {
    // Given
    when(analysisRepository.findByProjectAndName(testProject, "Test Analysis"))
        .thenReturn(Optional.of(testAnalysis));

    // When
    Optional<Analysis> result = analysisService.findByProjectAndName(testProject, "Test Analysis");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Test Analysis");
    verify(analysisRepository, times(1)).findByProjectAndName(testProject, "Test Analysis");
  }

  @Test
  @DisplayName("Should return empty when analysis not found by project and name")
  void shouldReturnEmptyWhenNotFoundByProjectAndName() {
    // Given
    when(analysisRepository.findByProjectAndName(testProject, "Nonexistent"))
        .thenReturn(Optional.empty());

    // When
    Optional<Analysis> result = analysisService.findByProjectAndName(testProject, "Nonexistent");

    // Then
    assertThat(result).isEmpty();
    verify(analysisRepository, times(1)).findByProjectAndName(testProject, "Nonexistent");
  }

  @Test
  @DisplayName("Should find analysis by id")
  void shouldFindById() {
    // Given
    when(analysisRepository.findById(1L)).thenReturn(Optional.of(testAnalysis));

    // When
    Optional<Analysis> result = analysisService.findById(1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(1L);
    verify(analysisRepository, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Should update analysis successfully")
  void shouldUpdateAnalysis() {
    // Given
    testAnalysis.setBeschreibung("Updated Description");
    when(analysisRepository.save(testAnalysis)).thenReturn(testAnalysis);

    // When
    Analysis result = analysisService.updateAnalysis(testAnalysis);

    // Then
    assertThat(result.getBeschreibung()).isEqualTo("Updated Description");
    verify(analysisRepository, times(1)).save(testAnalysis);
  }

  @Test
  @DisplayName("Should delete analysis by id")
  void shouldDeleteAnalysis() {
    // Given
    doNothing().when(analysisRepository).deleteById(1L);

    // When
    analysisService.deleteAnalysis(1L);

    // Then
    verify(analysisRepository, times(1)).deleteById(1L);
  }
}
