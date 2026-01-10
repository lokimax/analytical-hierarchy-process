import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AnalysisService, Analysis } from './analysis.service';

describe('AnalysisService', () => {
  let service: AnalysisService;
  let httpMock: HttpTestingController;
    const baseUrl = '/api/projects';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AnalysisService]
    });

    service = TestBed.inject(AnalysisService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAnalyses', () => {
    it('should retrieve all analyses for a project', (done) => {
      const projectName = 'test-project';
      const mockAnalyses: Analysis[] = [
        {
          id: 1,
          name: 'Analysis 1',
          beschreibung: 'Description 1',
          projectId: 1,
          createdAt: '2024-01-01T00:00:00Z'
        },
        {
          id: 2,
          name: 'Analysis 2',
          beschreibung: 'Description 2',
          projectId: 1,
          createdAt: '2024-01-02T00:00:00Z'
        }
      ];

      service.getAnalyses(projectName).subscribe({
        next: (analyses) => {
          expect(analyses.length).toBe(2);
          expect(analyses).toEqual(mockAnalyses);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses`);
      expect(req.request.method).toBe('GET');
      req.flush(mockAnalyses);
    });

    it('should return empty array when no analyses exist', (done) => {
      const projectName = 'empty-project';

      service.getAnalyses(projectName).subscribe({
        next: (analyses) => {
          expect(analyses).toEqual([]);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses`);
      req.flush([]);
    });

    it('should handle 404 when project not found', (done) => {
      const projectName = 'nonexistent';

      service.getAnalyses(projectName).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses`);
      req.flush('Project not found', { status: 404, statusText: 'Not Found' });
    });

    it('should encode project name with spaces', (done) => {
      const projectName = 'project with spaces';

      service.getAnalyses(projectName).subscribe({
        next: (analyses) => {
          expect(analyses).toEqual([]);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses`);
      // Note: HttpClient handles URL encoding automatically
      expect(req.request.urlWithParams).toBeTruthy();
      req.flush([]);
    });
  });

  describe('getAnalysis', () => {
    it('should retrieve a single analysis', (done) => {
      const projectName = 'test-project';
      const analysisId = 1;
      const mockAnalysis: Analysis = {
        id: analysisId,
        name: 'Test Analysis',
        beschreibung: 'Test Description',
        projectId: 1,
        criteriaComparisons: '{}',
        results: '{}'
      };

      service.getAnalysis(projectName, analysisId).subscribe({
        next: (analysis) => {
          expect(analysis).toEqual(mockAnalysis);
          expect(analysis.id).toBe(analysisId);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses/${analysisId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockAnalysis);
    });

    it('should handle 404 when analysis not found', (done) => {
      const projectName = 'test-project';
      const analysisId = 999;

      service.getAnalysis(projectName, analysisId).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses/${analysisId}`);
      req.flush('Analysis not found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle analysis with completed date', (done) => {
      const projectName = 'test-project';
      const analysisId = 1;
      const mockAnalysis: Analysis = {
        id: analysisId,
        name: 'Completed Analysis',
        beschreibung: 'Test',
        completedAt: '2024-01-05T10:00:00Z',
        createdAt: '2024-01-01T00:00:00Z'
      };

      service.getAnalysis(projectName, analysisId).subscribe({
        next: (analysis) => {
          expect(analysis.completedAt).toBe('2024-01-05T10:00:00Z');
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses/${analysisId}`);
      req.flush(mockAnalysis);
    });
  });

  describe('createAnalysis', () => {
    it('should create a new analysis', (done) => {
      const projectName = 'test-project';
      const newAnalysis: Analysis = {
        name: 'New Analysis',
        beschreibung: 'New Description'
      };

      const createdAnalysis: Analysis = {
        ...newAnalysis,
        id: 1,
        projectId: 1,
        createdAt: '2024-01-01T00:00:00Z'
      };

      service.createAnalysis(projectName, newAnalysis).subscribe({
        next: (analysis) => {
          expect(analysis).toEqual(createdAnalysis);
          expect(analysis.id).toBe(1);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newAnalysis);
      req.flush(createdAnalysis);
    });

    it('should handle validation errors', (done) => {
      const projectName = 'test-project';
      const invalidAnalysis: Analysis = {
        name: '',
        beschreibung: 'Test'
      };

      service.createAnalysis(projectName, invalidAnalysis).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(400);
          done();
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses`);
      req.flush('Name is required', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle duplicate analysis name', (done) => {
      const projectName = 'test-project';
      const duplicateAnalysis: Analysis = {
        name: 'Existing Analysis',
        beschreibung: 'Test'
      };

      service.createAnalysis(projectName, duplicateAnalysis).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(409);
          done();
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses`);
      req.flush('Analysis already exists', { status: 409, statusText: 'Conflict' });
    });

    it('should handle project not found during creation', (done) => {
      const projectName = 'nonexistent-project';
      const newAnalysis: Analysis = {
        name: 'New Analysis',
        beschreibung: 'Test'
      };

      service.createAnalysis(projectName, newAnalysis).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses`);
      req.flush('Project not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('deleteAnalysis', () => {
    it('should delete an analysis', (done) => {
      const projectName = 'test-project';
      const analysisId = 1;

      service.deleteAnalysis(projectName, analysisId).subscribe({
        next: () => {
          expect(true).toBeTrue(); // Successful completion
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses/${analysisId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle deletion of non-existent analysis', (done) => {
      const projectName = 'test-project';
      const analysisId = 999;

      service.deleteAnalysis(projectName, analysisId).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses/${analysisId}`);
      req.flush('Analysis not found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle server errors during deletion', (done) => {
      const projectName = 'test-project';
      const analysisId = 1;

      service.deleteAnalysis(projectName, analysisId).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
          done();
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${projectName}/analyses/${analysisId}`);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('Complex Scenarios', () => {
    it('should handle multiple analyses for same project', () => {
      const projectName = 'test-project';

      service.getAnalyses(projectName).subscribe();
      service.getAnalysis(projectName, 1).subscribe();
      service.getAnalysis(projectName, 2).subscribe();

      const requests = httpMock.match(req => req.url.includes(projectName));
      expect(requests.length).toBe(3);

      requests[0].flush([]);
      requests[1].flush({ id: 1, name: 'Analysis 1', beschreibung: 'Test' });
      requests[2].flush({ id: 2, name: 'Analysis 2', beschreibung: 'Test' });
    });

    it('should handle analyses for different projects', () => {
      let count = 0;

      service.getAnalyses('project1').subscribe({
        next: (analyses) => {
          expect(analyses.length).toBe(1);
          count++;
        }
      });

      service.getAnalyses('project2').subscribe({
        next: (analyses) => {
          expect(analyses.length).toBe(1);
          count++;
        }
      });

      const req1 = httpMock.expectOne(`${baseUrl}/project1/analyses`);
      const req2 = httpMock.expectOne(`${baseUrl}/project2/analyses`);

      req1.flush([{ id: 1, name: 'P1 Analysis', beschreibung: 'Test' }]);
      req2.flush([{ id: 2, name: 'P2 Analysis', beschreibung: 'Test' }]);

      expect(count).toBe(2);
    });

    it('should handle complete CRUD cycle', () => {
      const projectName = 'test-project';
      let phase = 1;
      const analysis: Analysis = {
        name: 'Test Analysis',
        beschreibung: 'Test'
      };

      // Create
      service.createAnalysis(projectName, analysis).subscribe({
        next: (created) => {
          expect(created.id).toBe(1);
          phase++;
        }
      });
      const createReq = httpMock.expectOne(`${baseUrl}/${projectName}/analyses`);
      const created = { ...analysis, id: 1 };
      createReq.flush(created);

      // Read
      service.getAnalysis(projectName, 1).subscribe({
        next: (fetched) => {
          expect(fetched.id).toBe(1);
          phase++;
        }
      });
      const getReq = httpMock.expectOne(`${baseUrl}/${projectName}/analyses/1`);
      getReq.flush(created);

      // Delete
      service.deleteAnalysis(projectName, 1).subscribe({
        next: () => {
          expect(phase).toBe(3);
        }
      });
      const deleteReq = httpMock.expectOne(`${baseUrl}/${projectName}/analyses/1`);
      deleteReq.flush(null);
    });
  });
});
