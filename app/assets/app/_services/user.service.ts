import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';

import 'rxjs/add/operator/map'

import { User } from '../_models/index';

@Injectable()
export class UserService {
    constructor(private http: Http) { }
    
    getAll() {
        console.log('Service: getAll');
        return this.http.get('/rest/api/1/users', this.auth()).map((response: Response) => response.json());
    }

    getByNickname(nickname: string) {
        console.log('Service: getById ' + nickname);
        return this.http.get('/rest/api/1/users/' + nickname, this.auth()).map((response: Response) => response.json());
    }

    create(user: User) {
        console.log('Service: create ' +  user);
        return this.http.post('/rest/api/1/users/', user).map((response: Response) => response.json());
    }

    update(user: User) {
        console.log('Service: update ' +  user);
        return this.http.put('/rest/api/1/users/', user, this.auth()).map((response: Response) => response.json());
    }

    delete(nickname: string) {
        console.log('Service: delete ' +  nickname);
        return this.http.delete('/rest/api/1/users/' + nickname, this.auth()).map((response: Response) => response.json());
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