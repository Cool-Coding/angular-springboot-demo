import { Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ValidationErrors, Validators} from '@angular/forms';
import {Observable, Observer, Subject} from 'rxjs';
import {Customer} from '../dto/Customer';
import { Utils} from '../util/Utils';
import {Room} from '../dto/Room';
import {RoomService} from '../room.service';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {

  validateForm: FormGroup;
  room: Room = new Room();
  rooms$: Observable<Room[]>;
  private searchTerms = new Subject<string>();

  submitForm = ($event) => {
    $event.preventDefault();

    const customer = new Customer();
    const room = new Room();

    for (const key in this.validateForm.controls) {
      if (key === 'rangeDate') {
        const valueArray = this.validateForm.controls[key].value;
        room.checkInDate =  Utils.isString(valueArray[0]) ? valueArray[0] : Utils.dateFormat(valueArray[0]);
        room.checkOutDate = Utils.isString(valueArray[1]) ? valueArray[1] : Utils.dateFormat(valueArray[1]);
      } else if ( key === 'roomNo') {
        room.roomNo = this.validateForm.controls[key].value;
      } else {
        customer[key] = this.validateForm.controls[key].value;
      }

      this.validateForm.controls[ key ].markAsDirty();
      this.validateForm.controls[ key ].updateValueAndValidity();
    }

    room.customers = [];
    room.customers.push(customer);

    this.roomService.login(room).subscribe((result) => {
      if (result !== undefined && result.success) {
        this.resetForm($event);
      }
    });

  }

  disabledDate = (current: Date): boolean => {
    const date = new Date();
    return current < Utils.getDate(date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + ' 00:00:00');
  }

  resetForm(e: MouseEvent): void {
    e.preventDefault();
    this.validateForm.reset();
    for (const key in this.validateForm.controls) {
      this.validateForm.controls[key].markAsPristine();
      this.validateForm.controls[key].updateValueAndValidity();
    }
  }

  idCardAsyncValidator = (control: FormControl) => Observable.create((observer: Observer<ValidationErrors>) => {
    setTimeout( () => {
      /**身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X */
      const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
      if (reg.test(control.value) === false) {
        observer.next({ error: true, wrong: true });
      } else {
        observer.next(null);
      }
      observer.complete();
    }, 500);
  })

  phoneNoAsyncValidator = (control: FormControl) => Observable.create((observer: Observer<ValidationErrors>) => {
    setTimeout( () => {
      /**手机号第1位肯定是1，第2位是3，4，5，7，8其中一个，剩余的9位在0-9之间*/
      const reg = /(^[1][3,4,5,7,8][0-9]{9}$)/;
      if (reg.test(control.value) === false) {
        observer.next({ error: true, wrong: true });
      } else {
        observer.next(null);
      }
      observer.complete();
    }, 500);
  })


  onRoomNoChange() {
    this.room.roomNo = this.validateForm.controls['roomNo'].value;
    this.searchTerms.next(this.room.roomNo);
  }

  constructor(private fb: FormBuilder, private roomService: RoomService) {
    this.validateForm = this.fb.group({
      name: [ '', [ Validators.required ]],
      idCard:   ['',  [ Validators.required] , [this.idCardAsyncValidator]],
      roomNo:   ['',  [ Validators.required]],
      phoneNo:  ['', [Validators.required], [this.phoneNoAsyncValidator]],
      rangeDate: ['', [Validators.required]],
      comment:  ['']
    });
  }

  ngOnInit() {
    this.rooms$ = this.searchTerms.pipe(
      // wait 300ms after each keystroke before considering the term
      debounceTime(300),

      // ignore new term if same as previous term
      distinctUntilChanged(),

      // switch to new search observable each time the term changes
      switchMap( (term: string) => this.roomService.searchRooms([{'key': 'roomNo', 'value': term}])),
    );
    this.rooms$.subscribe(rooms => {
      if (rooms != null && rooms.length === 1) {
        this.room = rooms[0];
      }
    });
  }
}
