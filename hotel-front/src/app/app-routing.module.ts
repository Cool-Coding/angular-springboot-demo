import { NgModule } from '@angular/core';
import { RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {LogoutComponent} from './logout/logout.component';
import {RoomManagementComponent} from './room-management/room-management.component';
import {CustomerManagementComponent} from './customer-management/customer-management.component';

const routes: Routes = [
  {path: '', redirectTo: '/roomManagement', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'logout', component: LogoutComponent},
  {path: 'roomManagement', component: RoomManagementComponent},
  {path: 'customerManagement', component: CustomerManagementComponent},
]
@NgModule({
  imports: [ RouterModule.forRoot(routes, { useHash: true })],
  exports: [ RouterModule ]
})

export class AppRoutingModule { }
