import { Injectable }                               from '@angular/core';
import { Http, Headers, RequestOptions, Response }  from '@angular/http';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/map';

@Injectable()
export class ReportService {
    
    constructor(private http: Http) { }

    getResultsForNode(projectname: string, prioritisation: string, parent: string ) {
        console.log('Service: getNodes');
        return this.http.get('/rest/api/1/projects/' + projectname + '/prioritisations/' + prioritisation +"/results/" + parent, this.auth()).
            map((response: Response) => response.json());
    }

    getInfluenceResults(projectname: string, prioritisation: string, child: string ) {
        console.log('Service: getInfluenceResults');
        
        return this.http.get('/rest/api/1/projects/' + projectname + '/prioritisations/' + prioritisation +"/influences/" + child, this.auth()).
            map((response: Response) => response.json());
    }
    
    getFullResults(projectname: string, prioritisation: string) {
        console.log('Service: getFullResults');
        let url = '/rest/api/1/projects/' + projectname + '/prioritisations/' + prioritisation + '/fullresult';
        console.log(url);
        return this.http.get(url, this.auth()).
            map((response: Response) => response.json());
    }
    
    getResult(projectname: string, prioritisation: string) {
        console.log('Service: getResult');
        return this.http.get('/rest/api/1/projects/' + projectname + '/prioritisations/' + prioritisation +"/result", this.auth()).
            map((response: Response) => response.json());
    }

    getResultsForChilds(projectname: string, prioritisation: string, parent: string ) {
        console.log('Service: getResultsForChilds');
        return this.http.get('/rest/api/1/projects/' + projectname + '/prioritisations/' + prioritisation +"/results/" + parent + "/childs", this.auth()).
            map((response: Response) => response.json());
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

/*
GET         /rest/api/1/projects/:projectname/prioritisations/:prioritisation/results/:parent       de.x132.results.controller.ResultController.getResultsForNode(projectname: String, prioritisation: String, parent: String)
GET         /rest/api/1/projects/:projectname/prioritisations/:prioritisation/influences/:child     de.x132.results.controller.ResultController.getInfluenceResults(projectname: String, prioritisation: String, child: String)
GET         /rest/api/1/projects/:projectname/prioritisations/:prioritisation/fullresult            de.x132.results.controller.ResultController.getFullResults(projectname: String, prioritisation: String)
GET         /rest/api/1/projects/:projectname/prioritisations/:prioritisation/result                de.x132.results.controller.ResultController.getResultsForLeafs(projectname: String, prioritisation: String)
GET         /rest/api/1/projects/:projectname/prioritisations/:prioritisation/results/:parent/childs de.x132.results.controller.ResultController.getResultsForChilds(projectname: String, prioritisation: String, parent: String)
*/