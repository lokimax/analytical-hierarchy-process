import { Injectable }                               from '@angular/core';
import { Http, Headers, RequestOptions, Response }  from '@angular/http';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/map';

import { Project }                                  from '../_models/index';

@Injectable()
export class ProjectService {
    constructor(private http: Http) { }

    getProjects(): Promise<Project[]> {
        console.log('ProjectService.getHeroes');
        return this.http.get('/rest/api/1/projects', this.auth())
                   .toPromise()
                   .then(response => response.json().data as Project[])
                   .catch(this.handleError);
    }
    
    getProject(name: string): Promise<Project> {
      return this.http.get('/rest/api/1/projects/' + name, this.auth())
            .toPromise()
            .then(response => {
                return response.json();
            });
    }
    
    update(name: string, project: Project): Promise<Project> {
        console.log('Service: update ' +  project);
        return this.http.put('/rest/api/1/projects/' + name, project, this.auth())
            .toPromise()
            .then(response => { 
                return response.json();
            });
    }
    
        
    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }
    
    getAll() {
        console.log('Service: getAll');
        return this.http.get('/rest/api/1/projects', this.auth()).map((response: Response) => response.json());
    }
    
    getByName(projectname: string) {
        console.log('Service: delete ' +  projectname);
        return this.http.get('/rest/api/1/projects/' + projectname, this.auth()).map((response: Response) => { response.json().data as Project; });
    }

    create(project: Project) {
        console.log('Service: create ' +  project);
        return this.http.post('/rest/api/1/projects/', project, this.auth()).map((response: Response) => response.json());
    }



    delete(projectname: string) {
        console.log('Service: delete ' +  projectname);
        return this.http.delete('/rest/api/1/projects/' + projectname, this.auth()).map((response: Response) => response.json());
    }
    
       // private helper methods
    private auth() {
        // create authorization header with jwt token
        let token = localStorage.getItem('token');
        if (token) {
            let headers = new Headers({ 'Authorization': 'Bearer ' + token });
            return new RequestOptions({ headers: headers });
        }
        return null;
    }
}
