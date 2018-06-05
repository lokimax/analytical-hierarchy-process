import { SingleResult } from './index';

export class AHPResult {
    singleResults: SingleResult[] = [];    
    parentNodeName: string;
    consistent: boolean;
    ci: number;
    cr: number;
}