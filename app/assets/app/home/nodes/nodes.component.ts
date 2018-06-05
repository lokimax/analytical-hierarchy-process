import { Component, OnInit, Input} from '@angular/core';
import { NodeService }                           from '../../_services/index';
import 'rxjs/add/operator/switchMap';
import { Location }                                 from '@angular/common';
import { ActivatedRoute, Params }                   from '@angular/router';

@Component({
    selector: 'nodes',
    templateUrl: './assets/app/home/nodes/nodes.component.html',
    providers: [NodeService]
})
export class NodesComponent implements OnInit {

    model: any = {};
    loading: boolean;
    nodes: Node[];
    selectedNode: Node;
    
    @Input() projectname: string;

    constructor(private nodeService: NodeService,
        private route: ActivatedRoute,
        private location: Location) { }

    ngOnInit(): void {
        console.log('NodesComponent.ngOnInit');
        this.loadNodes();
    }

    loadNodes() {
        console.log('NodesComponent.loadNodes');
        this.nodeService.getNodes(this.projectname).subscribe(nodes => { this.nodes = nodes; });
    }
    
    createNode(){
        this.loading = true;
        console.log('NodesComponent.createNode');
        this.nodeService.create(this.projectname, this.model).subscribe(
                data => {
                    this.loadNodes();
                    this.loading = false;
                    this.selectedNode = null;
                    this.model = {};
                },
                error => {
                    this.loading = false;
                }
            );
    }
    
    updateNode(){
        this.loading = true;
        console.log('NodesComponent.updateNode');
        this.nodeService.update(this.projectname, this.model.name, this.model).subscribe(
                data => {
                    this.loadNodes();
                    this.loading = false;
                },
                error => {
                    this.loading = false;
                }
        );
    }
    
    deleteNode(){
        console.log('NodesComponent.onSelect');
        this.nodeService.delete(this.projectname, this.selectedNode.name).subscribe(
                data => {
                    this.loadNodes();
                    this.loading = false;            
                    this.selectedNode = null;
                    this.model = {};
                },
                error => {
                    this.loading = false;
                }
            );
    }
    
    onSelect(node: Node): void {
        console.log('NodesComponent.onSelect');
        if(node == this.selectedNode) { 
            this.selectedNode = null;
            this.model = {};
        } else {
            this.model = node;
            this.selectedNode = node;
         }
    }
}