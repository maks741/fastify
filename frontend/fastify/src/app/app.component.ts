import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="container">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`
    * {
      margin: 0;
      padding: 0;
    }

    .container {
      width: 100%;
      height: 100%;
    }
  `]
})
export class AppComponent {
}
