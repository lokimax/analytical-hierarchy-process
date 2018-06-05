import { Component, OnInit, Input, OnChanges, SimpleChanges }      from '@angular/core';
import { ConnectionService }                        from '../../_services/index';
import 'rxjs/add/operator/switchMap';
import { Location }                                 from '@angular/common';
import { ActivatedRoute, Params }                   from '@angular/router';
import { Connection }                               from '../../_models/index';


@Component({
    selector: 'connections',
    templateUrl: './assets/app/home/connections/connections.component.html',
    providers: [ConnectionService]
})
export class ConnectionsComponent implements OnInit, OnChanges {

    model: any = {};
    loading: boolean;
    connections: Connection[];
    selectedConnection: Connection;
    
    @Input() parentnode: string;
    @Input() projectname: string;
    @Input() nodes: Node[];
    

    constructor(private connectionService: ConnectionService,
        private route: ActivatedRoute,
        private location: Location) { }

    ngOnInit(): void {
        console.log('ConnectionsComponent.ngOnInit');
        this.loadConnections();
    }
    
    ngOnChanges(changes: SimpleChanges) {
        if (changes.parentnode) {
            this.loadConnections();
        }
    }
    
    loadConnections() {
       console.log('ConnectionsComponent.loadNodes');
       if(this.parentnode != null){
           this.connectionService.getConnectionForNode(this.projectname, this.parentnode).subscribe(connections => { this.connections = connections; });
       } else {
           this.connectionService.getConnections(this.projectname).subscribe(connections => { this.connections = connections; });
       }
    }
        
    createNewConnection(source: string, target: string){
        this.loading = true;
        let connection = new Connection();
        connection.targetnode = target;
        connection.sourcenode = source;
        this.connectionService.createConnection(this.projectname, connection).subscribe(
            data => {
                this.loadConnections();
                this.loading = false;
            },
            error => {
                console.log(error);
                this.loading = false;
            }
        );
    }
    
    onSelect(connection: Connection): void {
        console.log('ConnectionsComponent.onSelect');
        if(connection == this.selectedConnection) { 
            this.selectedConnection = null;
            this.model = {};
        } else {
            this.model = connection;
            this.selectedConnection = connection;
         }
    }
    
    isSourceSelected(node: Node){
        if(this.selectedConnection != null) { 
            return this.selectedConnection.sourcenode==node.name; 
        } else { 
            return node.name === this.parentnode;
        }
        return null;
    }
    
    isTargetSelected(node: Node){
        if(this.selectedConnection != null) { 
            return this.selectedConnection.targetnode==node.name; 
        }
    }
    
    deleteConnection(source: string, target: string){
        this.loading = true;
        this.connectionService.deleteConnection(this.projectname, source, target).subscribe(
            data => {
                this.loadConnections();
                this.loading = false;
                this.selectedConnection = null;
                this.model = {};
            },
            error => {
                console.log(error);
                this.loading = false;
            }
        );
    }
}