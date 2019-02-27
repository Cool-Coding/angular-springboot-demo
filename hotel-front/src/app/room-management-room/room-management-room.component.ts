import {Component, forwardRef, Input, OnInit} from '@angular/core';
import {Room} from '../dto/Room';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Charge} from '../dto/Charge';
import {RoomService} from '../room.service';
import {Utils} from '../util/Utils';

@Component({
  selector: 'app-room-management-room',
  templateUrl: './room-management-room.component.html',
  styleUrls: ['./room-management-room.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: forwardRef(() => RoomManagementRoomComponent),
    }
  ]
})
export class RoomManagementRoomComponent implements OnInit, ControlValueAccessor {

  @Input() private _isVisible: boolean;
  @Input() room: Room;

  charge: Charge;

  message = {};

  constructor(private roomService: RoomService) { }

  ngOnInit() {
  }

  confirm() {
    if (this.charge.startDate as any instanceof Date) {
      this.charge.startDate = Utils.dateFormat(this.charge.startDate);
    }

    if (this.charge.endDate as any instanceof Date) {
      this.charge.endDate = Utils.dateFormat(this.charge.endDate);
    }

    let theSame = false;
    if (this.charge.id !== null) {
      const index = this.room.charges.findIndex(c => c.id === this.charge.id);
      const charge = this.room.charges[index];
      if (charge.timeUnit === this.charge.timeUnit
        && charge.count === this.charge.count
        && charge.money === this.charge.money
        && charge.startDate === this.charge.startDate
        && charge.endDate === this.charge.endDate) {
          theSame = true;
      }
    }

    if (!theSame) {
          // 检查输入的数据有效性
          if (!this.check()) {
            return;
          }

          // 如果要保存的房型与系统当前房型最新区间有重叠，则将当前区间截止日期修改为要保存起始日期减1秒
          // 如果用户只是更改了数量或价格而没有更改日期，则将原记录的截止日期等于开始日期
          const charge = this.findLatestCharge(this.charge.timeUnit);
          if (charge !== null && Utils.getDate(charge.endDate) >= Utils.getDate(this.charge.startDate)) {
            let date = Utils.getDate(this.charge.startDate).getTime() - 1;
            const startDate = Utils.getDate(charge.startDate).getTime();
            if (date < startDate) {
              date = startDate;
            }
            charge.endDate = Utils.dateFormat(new Date(date));
          }
          this.charge.id = null;
          this.room.charges.push(this.charge);
    }
    // 更改房间类型
    this.roomService.updateRoom({id: this.room.id, type: this.charge.timeUnit, charges: this.room.charges} as Room).subscribe( result => {
      if (result !== undefined && result.success !== undefined && result.success) {
        this.room.type = result.data.type;
        this.room.money = result.data.money;
        this.room.charges.splice(0, this.room.charges.length);
        if (result.data != null && result.data.charges !== undefined && result.data.charges != null && result.data.charges.length > 0) {
          result.data.charges.forEach(charge => this.room.charges.push(charge));
        }
      } else {
        this.room.charges = this.room.charges.filter(c => c.id !== null);
      }
      this.isVisible = false;
    });

  }

  // 找到某一房型最新的定价记录
  findLatestCharge(type: string): Charge {
    let index = -1;
    const charges = this.room.charges.filter(charge => charge.timeUnit === type);
    if (charges !== null && charges.length > 0) {
      index = 0;
      for (let i = 0; i < charges.length; i++ ) {
          if (Utils.getDate(charges[i].endDate) > Utils.getDate(charges[index].endDate)) {
            index = i;
          }
      }
    }
    return index === -1 ? null : charges[index];
  }

  timeUnitChange() {
      const charge = this.findLatestCharge(this.charge.timeUnit);
      if (charge !== null ) {
        // 使用 {...charge} 是因为返回的是引用，如果界面上值改变时，也会影响room中原有的charge值
        this.charge = {...charge};
      } else {
        this.charge = {id: null, timeUnit: this.charge.timeUnit, count: 1, money: null, startDate: '', endDate: '9999-12-31 23:59:59'};
      }
  }

  cancel() {
    this.isVisible = false;
  }

  get isVisible(): boolean {
    return this._isVisible;
  }

  set isVisible(value: boolean) {
    this._isVisible = value;
    this.propagateChange(this._isVisible);
  }

  propagateChange = (_: any) => {};

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
  }

  writeValue(value: any): void {
    if (value !== undefined) {
      if (value == null || value) {
        this.charge = null;
        if (this.room.charges != null) {
          const charge = this.findLatestCharge(this.room.type);
          if (charge !== null ) {
            this.charge = {...charge};
          } else {
            this.charge = {id: null, timeUnit: this.room.type, count: 1, money: null, startDate: '', endDate: '9999-12-31 23:59:59'};
          }
      }
      }
      this.isVisible = value;
    }
  }

  check(): boolean {
    let right = true;

    if (this.charge.count == null || this.charge.count === 0 || this.charge.count.toString().trim().length === 0) {
      this.message['count'] = '数量不能为空';
      right = false;
    }

    if (this.charge.money == null || this.charge.money === 0 || this.charge.money.toString().trim().length === 0) {
      this.message['money'] = '金额不能为空';
      right = false;
    }

    if (this.charge.timeUnit == null || this.charge.timeUnit.trim().length === 0) {
      this.message['timeUnit'] = '房型不能为空';
      right = false;
    }

    if (this.charge.startDate == null || this.charge.startDate.trim().length === 0) {
      this.message['startDate'] = '起始日期不能为空';
      right = false;
    }

    if (this.charge.endDate == null || this.charge.endDate.trim().length === 0) {
      this.message['endDate'] = '截止日期不能为空';
      right = false;
    }

    if (this.charge.startDate != null && this.charge.endDate !== null
      && Utils.getDate(this.charge.startDate) > Utils.getDate(this.charge.endDate)) {
      this.message['startDate'] = '起始日期不能晚于截止日期';
      right = false;
    }

    // 判断日期区间是否与现有日期区间是否有重叠
    /*if (this.charge.startDate != null && this.charge.endDate !== null) {
      const charges =  this.room.charges.filter(charge => charge.timeUnit === this.charge.timeUnit);
      for (let i = 0; i < charges.length; i++ ){
          const charge = charges[i];
          if (!(Utils.getDate(charge.startDate) > Utils.getDate(this.charge.endDate)
            || Utils.getDate(charge.endDate) < Utils.getDate(this.charge.startDate))) {
            this.message['endDate'] = '设置的日期区间与系统中价格日期区间[' + charge.startDate + '-' + charge.endDate + ']有重叠';
            right = false;
            break;
          }
      }
    }*/
    return right;
  }
}
