import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="row">
      <div class="col-md-8">
        <h1>About AHP</h1>
        <p>
          The Analytical Hierarchy Process (AHP) is a multi-criteria decision-making approach 
          developed by Thomas L. Saaty in the late 1970s.
        </p>
        <h3>Key Features</h3>
        <ul class="list-group">
          <li class="list-group-item">Hierarchical problem decomposition</li>
          <li class="list-group-item">Pairwise comparison of alternatives</li>
          <li class="list-group-item">Consistency analysis</li>
          <li class="list-group-item">Priority calculation</li>
        </ul>
      </div>
    </div>
  `,
  styles: [`
    h1 {
      color: #333;
      margin-bottom: 2rem;
    }
    
    h3 {
      color: #555;
      margin-top: 2rem;
      margin-bottom: 1rem;
    }
  `]
})
export class AboutComponent {}
