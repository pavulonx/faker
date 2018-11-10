import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {WorkspaceComponent} from "./workspace/workspace.component";
import {AboutComponent} from "./about/about.component";
import {EndpointDetailsComponent} from "./workspace/endpoint-details/endpoint-details.component";

const routes: Routes = [
  {
    path: '', redirectTo: '/workspace', pathMatch: 'full'
  }, {
    path: 'workspace',
    component: WorkspaceComponent,// pathMatch: 'full',
    children: [
      // {path: '', component: EndpointDetailsComponent},
      {path: ':endpointUuid', component: EndpointDetailsComponent},
      // {path: 'uuid4', component: EndpointDetailsComponent},
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
