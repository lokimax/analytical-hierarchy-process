import { Injectable }                               from '@angular/core';
import { Http, Headers, RequestOptions, Response }  from '@angular/http';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/map';

import { Prioritisation }                                      from '../_models/index';
import { Comparison }                                      from '../_models/index';


@Injectable()
export class PrioritisationService {
    constructor(private http: Http) { }
    
    getAll(project: string) {
        console.log('Service: getAll');
        return this.http.get('/rest/api/1/projects/'+ project +'/prioritisations', this.auth())
            .map((response: Response) => response.json());
    }
    
    create(project: string, prioritisation: Prioritisation ){
        console.log('Service: getAll');
        return this.http.post('/rest/api/1/projects/'+ project +'/prioritisations/', prioritisation , this.auth())
            .map((response: Response) => response.json());
    }
    
    getPriorisation(projectName: string, prioritisationName: string){
        console.log('Service: getPriorisation');
        return this.http.get('/rest/api/1/projects/'+ projectName +'/prioritisations/' + prioritisationName, this.auth())
            .map((response: Response) => response.json());
    }
    
    delete(projectName: string, prioritisationName: string){
        console.log('Service: getAll');
        return this.http.delete('/rest/api/1/projects/'+ projectName +'/prioritisations/' + prioritisationName, this.auth())
            .map((response: Response) => response.json());
    }
    
    getComparison(projectname: string, prioritisation: string, parent: string, leftnode: string, rightnode: string){
        return this.http.delete('/rest/api/1/projects/' + projectname + '/prioritisations/' + prioritisation + '/comparisons/' + parent + "/" + leftnode + "/" + rightnode,  this.auth())
            .map((response: Response) => response.json());
    }
    
    putComparison(projectname: string, prioritisation: string, comparison: Comparison){
        return this.http.put('/rest/api/1/projects/' + projectname + '/prioritisations/' + prioritisation + '/comparisons/', comparison ,  this.auth())
            .map((response: Response) => response.json());
    }

    
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