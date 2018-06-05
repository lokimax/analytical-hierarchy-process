import { Injectable }                               from '@angular/core';
import { Http, Headers, RequestOptions, Response }  from '@angular/http';

import {Observable}                                 from 'rxjs/Observable';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/map';

import { Connection }                               from '../_models/index';

@Injectable()
export class ConnectionService {
        
    constructor(private http: Http) { }
         
    getConnections(projectname: string) {
        console.log('Service: getConnection');
        return this.http.get('/rest/api/1/projects/' + projectname + '/connections', this.auth()).
            map((response: Response) => response.json());
    }
    
    getConnectionForNode(projectname: string, startnode: string) {
      console.log('Service: getConnection');         
      return this.http.get('/rest/api/1/projects/' + projectname + '/nodes/' + startnode + '/connections' , this.auth()).
            map((response: Response) => response.json());
    }
    
    
    createConnection(projectname: string, connection: Connection) {
      console.log('Service: createConnection');         
      return this.http.post('/rest/api/1/projects/' + projectname + '/connections/', connection,  this.auth())
            .map((response: Response) => response.json());
    }
    
    deleteConnection(projectname: string, source: string, target: string) {
      console.log('Service: deleteConnection');         
      return this.http.delete('/rest/api/1/projects/' + projectname + '/nodes/' + source + '/connections/' + target,  this.auth())
            .map((response: Response) => response.json());
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
