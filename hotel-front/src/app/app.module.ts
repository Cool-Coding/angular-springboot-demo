import { BrowserModule } from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import {ReactiveFormsModule, FormsModule} from '@angular/forms';
import {NgZorroAntdModule, NZ_I18N, zh_CN} from 'ng-zorro-antd';
import { AppComponent } from './app.component';
import { SiderComponent } from './sider/sider.component';
import {HttpClientModule} from '@angular/common/http';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { RoomNoSearchComponent } from './room-no-search/room-no-search.component';
import { registerLocaleData } from '@angular/common';
import zh from '@angular/common/locales/zh';
import { AppRoutingModule } from './app-routing.module';
import { RoomManagementComponent } from './room-management/room-management.component';
import { CustomerManagementComponent } from './customer-management/customer-management.component';
import { RoomManagementBedComponent } from './room-management-bed/room-management-bed.component';
import { RoomManagementRoomComponent } from './room-management-room/room-management-room.component';

registerLocaleData(zh);

@NgModule({
  declarations: [
    AppComponent,
    SiderComponent,
    LoginComponent,
    LogoutComponent,
    RoomNoSearchComponent,
    RoomManagementComponent,
    CustomerManagementComponent,
    RoomManagementBedComponent,
    RoomManagementRoomComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    NgZorroAntdModule,
    AppRoutingModule,
    HttpClientModule,
  ],
  providers: [{ provide: NZ_I18N, useValue: zh_CN }],
  bootstrap: [AppComponent]
})
export class AppModule { }
