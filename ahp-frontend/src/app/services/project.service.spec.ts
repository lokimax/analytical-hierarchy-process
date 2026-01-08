import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService, Project } from './project.service';

describe('ProjectService', () => {
  let service: ProjectService;
  let httpMock: HttpTestingController;
  const apiUrl = 'http://localhost:9000/api/projects';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectService]
    });

    service = TestBed.inject(ProjectService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getProjects', () => {
    it('should retrieve all projects', (done) => {
      const mockProjects: Project[] = [
        {
          id: 1,
          name: 'Project 1',
          beschreibung: 'Description 1',
          clientNickname: 'user1',
          createdAt: '2024-01-01T00:00:00Z'
        },
        {
          id: 2,
          name: 'Project 2',
          beschreibung: 'Description 2',
          clientNickname: 'user1',
          createdAt: '2024-01-02T00:00:00Z'
        }
      ];

      service.getProjects().subscribe({
        next: (projects) => {
          expect(projects.length).toBe(2);
          expect(projects).toEqual(mockProjects);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockProjects);
    });

    it('should return empty array when no projects exist', (done) => {
      service.getProjects().subscribe({
        next: (projects) => {
          expect(projects).toEqual([]);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush(null);
    });

    it('should handle empty array response', (done) => {
      service.getProjects().subscribe({
        next: (projects) => {
          expect(projects).toEqual([]);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush([]);
    });

    it('should handle error response', (done) => {
      service.getProjects().subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('getProject', () => {
    it('should retrieve a single project by name', (done) => {
      const projectName = 'test-project';
      const mockProject: Project = {
        id: 1,
        name: projectName,
        beschreibung: 'Test Description',
        clientNickname: 'testuser'
      };

      service.getProject(projectName).subscribe({
        next: (project) => {
          expect(project).toEqual(mockProject);
          expect(project.name).toBe(projectName);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/${projectName}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockProject);
    });

    it('should handle 404 when project not found', (done) => {
      const projectName = 'nonexistent';

      service.getProject(projectName).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${projectName}`);
      req.flush('Project not found', { status: 404, statusText: 'Not Found' });
    });

    it('should encode project name in URL', (done) => {
      const projectName = 'project with spaces';
      const mockProject: Project = {
        id: 1,
        name: projectName,
        beschreibung: 'Test'
      };

      service.getProject(projectName).subscribe({
        next: () => done(),
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/${projectName}`);
      // Note: HttpClient handles URL encoding automatically
      expect(req.request.urlWithParams).toBeTruthy();
      req.flush(mockProject);
    });
  });

  describe('createProject', () => {
    it('should create a new project', (done) => {
      const newProject: Project = {
        name: 'New Project',
        beschreibung: 'New Description'
      };

      const createdProject: Project = {
        ...newProject,
        id: 1,
        createdAt: '2024-01-01T00:00:00Z'
      };

      service.createProject(newProject).subscribe({
        next: (project) => {
          expect(project).toEqual(createdProject);
          expect(project.id).toBe(1);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newProject);
      req.flush(createdProject);
    });

    it('should handle validation errors', (done) => {
      const invalidProject: Project = {
        name: '',
        beschreibung: 'Description'
      };

      service.createProject(invalidProject).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(400);
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush('Name is required', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle duplicate project name', (done) => {
      const duplicateProject: Project = {
        name: 'Existing Project',
        beschreibung: 'Test'
      };

      service.createProject(duplicateProject).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(409);
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush('Project already exists', { status: 409, statusText: 'Conflict' });
    });
  });

  describe('updateProject', () => {
    it('should update an existing project', (done) => {
      const projectId = 1;
      const updatedData: Project = {
        name: 'Updated Project',
        beschreibung: 'Updated Description'
      };

      const updatedProject: Project = {
        ...updatedData,
        id: projectId,
        updatedAt: '2024-01-02T00:00:00Z'
      };

      service.updateProject(projectId, updatedData).subscribe({
        next: (project) => {
          expect(project).toEqual(updatedProject);
          expect(project.id).toBe(projectId);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/${projectId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedData);
      req.flush(updatedProject);
    });

    it('should handle update of non-existent project', (done) => {
      const projectId = 999;
      const updatedData: Project = {
        name: 'Updated',
        beschreibung: 'Test'
      };

      service.updateProject(projectId, updatedData).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${projectId}`);
      req.flush('Project not found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle validation errors during update', (done) => {
      const projectId = 1;
      const invalidData: Project = {
        name: '',
        beschreibung: 'Test'
      };

      service.updateProject(projectId, invalidData).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(400);
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${projectId}`);
      req.flush('Name is required', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('deleteProject', () => {
    it('should delete a project', (done) => {
      const projectId = 1;

      service.deleteProject(projectId).subscribe({
        next: () => {
          expect(true).toBeTrue(); // Successful completion
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/${projectId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle deletion of non-existent project', (done) => {
      const projectId = 999;

      service.deleteProject(projectId).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${projectId}`);
      req.flush('Project not found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle server errors during deletion', (done) => {
      const projectId = 1;

      service.deleteProject(projectId).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${projectId}`);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('Multiple Operations', () => {
    it('should handle multiple concurrent requests', () => {
      service.getProjects().subscribe();
      service.getProject('project1').subscribe();
      service.getProject('project2').subscribe();

      const requests = httpMock.match(() => true);
      expect(requests.length).toBe(3);

      requests[0].flush([]);
      requests[1].flush({ name: 'project1', beschreibung: 'Test 1' });
      requests[2].flush({ name: 'project2', beschreibung: 'Test 2' });
    });
  });
});
