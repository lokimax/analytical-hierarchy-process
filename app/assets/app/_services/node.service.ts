import { Injectable }                               from '@angular/core';
import { Http, Headers, RequestOptions, Response }  from '@angular/http';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/map';

import { Node }                                      from '../_models/index';

@Injectable()
export class NodeService {
        
    constructor(private http: Http) { }
         
    getNodes(projectname: string) {
        console.log('Service: getNodes');
        return this.http.get('/rest/api/1/projects/' + projectname + '/nodes', this.auth()).
            map((response: Response) => response.json());
    }
    
    getNode(projectname: string, name: string): Promise<Node> {
      return this.http.get('/rest/api/1/projects/' + projectname + '/nodes/' + name, this.auth())
            .toPromise()
            .then(response => {
                return response.json();
            });
    }
    
    create(projectname: string, node: Node) {
        console.log('Service: create ' +  node);
        return this.http.post('/rest/api/1/projects/' + projectname + '/nodes/', node, this.auth())
            .map((response: Response) => response.json());
    }
    
    
    update(projectname: string, name: string, node: Node) {
        console.log('Service: update ' +  node);
        return this.http.put('/rest/api/1/projects/' + projectname + '/nodes/' + name, node, this.auth())
            .map((response: Response) => response.json());
    }
    
    delete(projectname: string,  nodename: string,) {
        console.log('Service: delete ' +  nodename);
        return this.http.delete('/rest/api/1/projects/' + projectname + '/nodes/'+ nodename, this.auth()).map((response: Response) => response.json());
    }
    
        
    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
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
