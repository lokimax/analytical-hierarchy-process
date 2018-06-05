import { Injectable }               from '@angular/core';
import { Http, Headers, Response }  from '@angular/http';
import { Observable }               from 'rxjs/Observable';

import 'rxjs/add/operator/map'


@Injectable()
export class AuthenticationService {
        
    constructor(private http: Http) { }

    login(username: string, password: string) {
      let headers = new Headers();
      headers.append("authorization", "Basic " + btoa(username + ":" + password)); 
      return this.http.get('/rest/api/1/users/login/', {headers: headers}).map((response: Response) => {
                // login successful if there's a jwt token in the response
                let token = response.text();
                if (token) {
                    // store user details and jwt token in local storage to keep user logged in between page refreshes
                    localStorage.setItem('token', token);
                }
            });
    }

    logout() {
        // remove user from local storage to log user out
        localStorage.removeItem('token');
    }
}