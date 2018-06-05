import { Component, OnInit }                        from '@angular/core';
import { Router }                                   from '@angular/router';
import { ReportService }                            from '../../_services/index';
import { NodeService }                              from '../../_services/index';
import { AlertService }                             from '../../_services/index';
import { FullResult }                               from '../../_models/index';
import { AHPResult }                                from '../../_models/index';
import { Dataset }                                  from '../../_models/index';
import { ChildResult }                              from '../../_models/index';
import { SingleResult }                             from '../../_models/index';
import { SolvingResult }                            from '../../_models/index';
import { Node }                                     from '../../_models/index';

import { ActivatedRoute, Params }                   from '@angular/router';
import { Location }                                 from '@angular/common';


@Component({
    selector: 'reports',
    templateUrl: './assets/app/home/reports/reports.component.html'
})
export class ReportsComponent implements OnInit {

    projektname: string;
    prioritisierung: string;
    report: FullResult;
    nodes: Node[] = [];
    selectedNode: Node;
    singleReport: AHPResult;
    singleIncResults: SolvingResult;
    childResult: ChildResult;
    fullResult: FullResult;

    constructor(private reportService: ReportService, private nodeService: NodeService,
        private route: ActivatedRoute,
        private location: Location,
        private alertService: AlertService) { }

    ngOnInit() {
        console.log('ReportsComponent.ngOnInit');
        this.route.params.subscribe(params => {
            this.projektname = params['project'];
            this.prioritisierung = params['prioritisation'];
            //this.loadReports();
            this.loadNodes();
            this.loadResults();
        });
    }

    loadNodes() {
        console.log('ReportsComponent.loadNodes');
        this.nodeService.getNodes(this.projektname).subscribe(nodes => { this.nodes = nodes; });
    }

    // Radar
    public chartOutLabels: string[] = [];
    public chartOutData: any = [];
    public chartOutType: string = 'radar';

    public chartIncLabels: string[] = [];
    public chartIncData: any = [];
    public chartIncType: string = 'radar';

    public chartFullLabels: string[] = [];
    public chartFullData: any = [];
    public chartFullType: string = 'radar';
    
    public chartGlobalLabels: string[] = [];
    public chartGlobalData: any = [];
    public chartGlobalType: string = 'pie';
    
    // events
    public chartClicked(e: any): void {
        console.log(e);
    }

    public chartHovered(e: any): void {
        console.log(e);
    }

    onSelect(node: Node): void {
        console.log('NodesComponent.onSelect');
        if (node == this.selectedNode) {
            this.selectedNode = null;
        } else {
            this.selectedNode = node;
            this.loadIncResultsForNode();
            this.loadOutResultsForNode();
            this.loadReportServices();
        }
    }

    loadResults(){
         this.reportService.getResult(this.projektname, this.prioritisierung).subscribe(
                data => {
                    if (data != null) {
                        this.fullResult=data;
                        this.chartGlobalLabels = [];
                        let datasets: Dataset[] = [];
                        datasets.push(new Dataset());
                        datasets[0].label = data.parentNode;
                        data.results.forEach((item, index) => {
                            this.chartGlobalLabels[index] = item.nodename;
                            datasets[0].data.push(item.value * 100);
                        });
                        this.chartGlobalData = datasets;
                    }
                }, error => {});
    }
    
    
    loadReportServices() {
        if (this.selectedNode != null) {
            this.reportService.getResultsForChilds(this.projektname, this.prioritisierung, this.selectedNode.name)
                .subscribe(
                data => {
                    if (data != null) {
                        this.childResult = data;
                        if (data.solvingResults.length <= 5) {
                            this.chartFullType = 'pie';
                        } else {
                            this.chartFullType = 'radar';
                        }
                        let labels: string[] = [];
                        let datasets: Dataset[] = [];
                        data.solvingResults.forEach((item, index) => {
                            labels[index] = item.parentNode;
                            //console.log(item.results);
                            for (let entry of item.results) {
                                let found: boolean;
                                found = false;
                                for (let dataEntry of datasets) {
                                    if (dataEntry.label == entry.nodename) {
                                        found = true;
                                        dataEntry.data.push(entry.value * 100);
                                    }
                                }
                                if (found == false) {
                                    let dataset = new Dataset();
                                    dataset.label = entry.nodename;
                                    dataset.data.push(entry.value*100);
                                    datasets.push(dataset);
                                }
                            }
                        });
                        this.chartFullLabels = labels;
                        this.chartFullData = datasets;
                        console.log(JSON.stringify(datasets));
                        console.log(JSON.stringify(labels));
                    }
                },
                error => { this.alertService.error(error); }
                );
        }
    }

    loadOutResultsForNode() {
        this.singleReport = null;
        this.reportService.getResultsForNode(this.projektname, this.prioritisierung, this.selectedNode.name)
            .subscribe(
            singleReport => {
                if (singleReport != null) {
                    this.singleReport = singleReport;
                    if (singleReport != null && singleReport.singleResults.length <= 5) {
                        this.chartOutType = 'pie';
                    } else {
                        this.chartOutType = 'radar';
                    }
                    this.chartOutLabels = [];
                    let datasets: Dataset[] = [];
                    datasets.push(new Dataset());
                    datasets[0].label = singleReport.parentNodeName;
                    singleReport.singleResults.forEach((item, index) => {
                        this.chartOutLabels[index] = item.nodename;
                        datasets[0].data.push(item.value * 100);
                    });
                    this.chartOutData = datasets;
                } else {
                    this.singleReport = null;
                }
            },
            error => { this.alertService.error(error); }
            );
    }


    loadIncResultsForNode() {
        this.singleIncResults = null;
        this.reportService.getInfluenceResults(this.projektname, this.prioritisierung, this.selectedNode.name)
            .subscribe(
            singleIncResults => {
                if (singleIncResults != null) {
                    this.singleIncResults = singleIncResults;
                    if (singleIncResults != null && singleIncResults.singleResults.length <= 5) {
                        this.chartIncType = 'pie';
                    } else {
                        this.chartIncType = 'radar';
                    }
                    this.chartIncLabels = [];
                    let datasets: Dataset[] = [];
                    datasets.push(new Dataset());
                    datasets[0].label = this.selectedNode.name;
                    singleIncResults.singleResults.forEach((item, index) => {
                        this.chartIncLabels[index] = item.nodename;
                        datasets[0].data.push(item.value * 100);
                    });
                    this.chartIncData = datasets;
                } else {
                    this.singleReport = null;
                }
            },
            error => { this.alertService.error(error); }
            );
    }
}