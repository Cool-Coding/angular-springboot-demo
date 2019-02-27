import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup, Validators
} from '@angular/forms';
import {CustomerService} from '../customer.service';
import {RoomService} from '../room.service';
import {SearchCondition} from '../dto/SearchCondition';
import {Logout} from '../dto/Logout';
import {NzNotificationService} from 'ng-zorro-antd';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

  validateForm: FormGroup;
  isCollapse = true;
  rooms = [];
  isVisible = false;
  logoutDto: Logout;

  resetForm(e: MouseEvent): void {
    e.preventDefault();
    this.validateForm.reset();
  }


  toggleCollapse(): void {
    this.isCollapse = !this.isCollapse;
  }

  submitForm = ($event, value) => {
    $event.preventDefault();

    const conditions = [];

    for (const key in this.validateForm.controls) {
      if (this.validateForm.controls[key].value !== null && this.validateForm.controls[key].value.trim() !== '') {
      const searchCondition = new SearchCondition();
      searchCondition.key = key;
      searchCondition.value = this.validateForm.controls[key].value;
      conditions.push(searchCondition);
    }

      this.validateForm.controls[ key ].markAsDirty();
      this.validateForm.controls[ key ].updateValueAndValidity();
    }

      this.roomService.searchRooms(conditions).subscribe(rooms => {this.rooms = rooms.
      map( (room) => { room['expand'] = false; return room; }); });

  }

  logout(roomId: number) {
    this.roomService.logout(roomId).subscribe((result) => {
      if (result !== undefined && result.success !== undefined) {
        if (result.success) {
          // @ts-ignore
          this.logoutDto = result.data;
          this.isVisible = true;
          this.rooms.forEach((room) => {
            if (room.id === roomId) {
              room.checkInDate = '';
              room.checkOutDate = '';
              room.customers = [];
            }
            ;
            return room;
          });
          this.notification.create('success', '退房成功', '入住时间:' + this.logoutDto.interval
            + this.logoutDto.type.substr(0, this.logoutDto.type.length - 1)
            + ';总房费:' + this.logoutDto.sum + '元', {nzDuration: 0});
        } else {
          this.notification.create('error', '退房失败', result.message, {nzDuration: 0});
        }
      }
    });
  }

  confirmLogout(){
    this.isVisible = false;
  }

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    private roomService: RoomService,
    private notification: NzNotificationService) {
    this.validateForm = this.fb.group({
      name: [ ''],
      idCard:   ['', [Validators.pattern(/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/)]],
      roomNo:   [''],
      phoneNo:  ['', [Validators.pattern(/(^[1][3,4,5,7,8][0-9]{9}$)/)]],
    });
  }

  ngOnInit() {

  }

}
