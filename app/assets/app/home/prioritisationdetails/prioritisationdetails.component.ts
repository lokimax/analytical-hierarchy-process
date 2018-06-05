import { Component, OnInit }                        from '@angular/core';
import 'rxjs/add/operator/switchMap';
import { Location }                                 from '@angular/common';
import { ActivatedRoute, Params }                   from '@angular/router';
import { PrioritisationService }                    from '../../_services/index';
import { AlertService }                             from '../../_services/index';
import { ProjectService }                           from '../../_services/index';
import { Prioritisation }                           from '../../_models/index';
import { Comparison }                               from '../../_models/index';


@Component({
    selector: 'prioritisation',
    templateUrl: './assets/app/home/prioritisationdetails/prioritisationdetails.component.html'
})
export class PrioritisationDetailsComponent implements OnInit {

    currentComparison: Comparison;
    currentIndex: number;
    
    messages: any[] = [
            'keine Bedeutung ',
            'sehr geringere Bedeutung',
            'deutlich kleinere Bedeutung',
            'etwas kleinere Bedeutung',
            'gleiche Bedeutung', 
            'etwas größere Bedeutung',
            'deutlich größere Bedeutung',
            'sehr viel größere Bedeutung',
            'absolut dominierende bedeutung'];
    
    prioritisation: Prioritisation;
    projectname: string;
    prioritisationame: string;

    parentNode: Node;
    leftNode: Node;
    rightNode: Node;
    
    currentText: string;

    constructor(
        private prioritisationService: PrioritisationService,
        private route: ActivatedRoute,
        private location: Location,
        private alertService: AlertService) { }

    ngOnInit() {
        console.log('PrioritisationDetailsComponent.ngOnInit');
        this.currentIndex = 1;
        this.route.params.subscribe(params => {
            this.projectname = params['project'];
            this.prioritisationame = params['prioritisation'];
            this.loadPriorisation();
        });

    }

    loadPriorisation() {
        this.prioritisationService.getPriorisation(this.projectname, this.prioritisationame)
            .subscribe(prioritisation => {
                this.prioritisation = prioritisation;
                if (this.prioritisation.comparisons.length > 0) {
                    this.currentComparison = this.prioritisation.comparisons[this.currentIndex - 1];
                    this.changeText(this.currentComparison.weight);
                }
            });
    }
    
    save(){
        this.prioritisationService.putComparison(this.projectname, this.prioritisationame, this.currentComparison)
            .subscribe(
            data => {

            },
            error => {
                this.alertService.error(error);
            });

    }

    goNext() {
        if (this.currentIndex + 1 <= this.prioritisation.comparisons.length) {
            //this.save();
            this.currentIndex++;
            this.currentComparison = this.prioritisation.comparisons[this.currentIndex - 1]
            this.changeText(this.currentComparison.weight);
        }
    }

    goPrevious() {
        if (this.currentIndex - 1 > 0) {
            //this.save();
            this.currentIndex--;
            this.currentComparison = this.prioritisation.comparisons[this.currentIndex - 1]
            this.changeText(this.currentComparison.weight);
        }
    }
    
    onChange(newValue) {
        console.log(newValue);
        this.changeText(newValue);
        this.save();
    }
    
    changeText(value){
        this.currentText =  this.currentComparison.leftNodeName + ' hat ' + this.messages[value+4] + ' wie ' + this.currentComparison.rightNodeName;
    }
}