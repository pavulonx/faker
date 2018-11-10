import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {WorkspaceComponent} from "./workspace/workspace.component";
import {AboutComponent} from "./about/about.component";

const routes: Routes = [
  {
    path: '', redirectTo: '/workspace', pathMatch: 'full'
  }, {
    path: 'workspace',
    component: WorkspaceComponent, pathMatch: 'full',
    children: [
      // {path: 'light', component: LightBlueComponent},
      // {path: 'medium', component: MediumBlueComponent},
      // {path: 'dark', component: DarkBlueComponent}
    ]
  }, {
    path: 'about',
    component: AboutComponent,
    pathMatch: 'full',
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
