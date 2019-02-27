import {Component, forwardRef, Input, OnInit} from '@angular/core';
import {Bed} from '../dto/Bed';
import {RoomService} from '../room.service';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Room} from '../dto/Room';

/**
 * 将isVisible进行双向绑定，这样外部组件在使用此组件时，可以控制显示与隐藏，否则在在此组件内隐藏之后，当再次点击时，由于外部组件的属性没有变化，不会将值再次传入此组件内，
 * 不会触发再次显示
 */
@Component({
  selector: 'app-room-management-bed',
  templateUrl: './room-management-bed.component.html',
  styleUrls: ['./room-management-bed.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: forwardRef(() => RoomManagementBedComponent),
    }
  ]
})

export class RoomManagementBedComponent implements OnInit, ControlValueAccessor {

  @Input() roomId: number;
  @Input() beds: Bed[];
  @Input() private _isVisible: boolean;

  dataSet = [];
  editCache = {};

  isOkLoading = false;

  startEdit(key: string): void {
    this.editCache[ key ].edit = true;
  }

  cancelEdit(key: string): void {
    this.editCache[ key ].edit = false;
  }

  saveEdit(key: number): void {
    const index = this.dataSet.findIndex(item => item.key === key);
    Object.assign(this.dataSet[ index ], this.editCache[ key ].data);
    this.editCache[ key ].edit = false;
  }

  add() {
    this.dataSet = [ ...this.dataSet, {key: this.dataSet.length, id: null, name: '', type: '单人床', size: ''}];
    this.updateEditCache(true);
  }

  delete(key: number): void {
    this.dataSet = this.dataSet.filter(d => d.key !== key);
    delete this.editCache[key];
  }

  updateEditCache(edit: boolean = false): void {
    this.dataSet.forEach(item => {
      if (!this.editCache[ item.key ]) {
        this.editCache[ item.key ] = {
          edit: edit,
          data: { ...item }
        };
      }
    });
  }

  constructor(private roomService: RoomService) { }

  ngOnInit() {

  }

  confirm() {
    // todo: 若没有变化，则无需调用后端
    let theSame = true;
    if (this.beds.length === this.dataSet.length) {
      for(let i = 0; i < this.beds.length; i++ ){
        const bed = this.beds[i];
        const index = this.dataSet.findIndex(data => data.id === bed.id);
        if (index !== -1) {
          const newBed = this.dataSet[index];
          if (newBed.id === bed.id
          &&  newBed.name === bed.name
          && newBed.type === bed.type
          && newBed.size === bed.size) {

          }  else {
            theSame = false;
            break;
          }
        } else {
          theSame = false;
          break;
        }
      }
    } else {
      theSame = false;
    }

    if (theSame) {
      this.isVisible = false;
      return;
    }

    this.isOkLoading = true;
    this.roomService.updateRoom({id: this.roomId, beds: this.dataSet} as Room).subscribe( result => {
      if (result !== undefined && result.success !== undefined && result.success) {
          this.beds.splice(0, this.beds.length);
          if (result.data != null && result.data.beds !== undefined && result.data.beds != null && result.data.beds.length > 0) {
            result.data.beds.forEach(bed => this.beds.push(bed));
          }
      }
      this.isVisible = false;
      this.isOkLoading = false;
    });
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
      // 外部组件，点击修改床铺时，value第一次是null,null时变显示了，此时dataset为空
      if (value == null || value) {
        this.init();
        // 此处要复制，而不是引用，这样内部组件值的改变不会引响到组件外，不能使用this.dataSet = [...this.beds];，因为复制的是引用
        this.beds.forEach((item, index) => this.dataSet.push({key: index, ...item}));
        this.updateEditCache();
      }
      this.isVisible = value;
    }
  }

  init(): void {
    this.dataSet = [];
    this.editCache = {};
  }
}
