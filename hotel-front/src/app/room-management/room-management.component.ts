import { Component, OnInit } from '@angular/core';
import {RoomService} from '../room.service';
import {Room} from '../dto/Room';
import {Bed} from '../dto/Bed';

@Component({
  selector: 'app-room-management',
  templateUrl: './room-management.component.html',
  styleUrls: ['./room-management.component.css']
})
export class RoomManagementComponent implements OnInit {
  pageIndex = 1;
  pageSize = 10;
  total = 1;
  dataSet = [];
  loading = true;
  sortValue = null;
  sortKey = null;
  rooms = [];
  unused = 2;

  // 床铺管理
  roomId: number;
  isVisible = false;
  beds: Bed[] = [];

  // 更改房型
  isRoomVisible = false;
  room: Room;

  // 新增客房
  isAddRoomVisible = false;
  isOkLoading = false;
  message = {};

  filterRoomStatus = [
    { text: '空房', value: '0' },
    { text: '有客', value: '1' },
    { text: '不可订', value: '2'},
  ];

  searchRoomStatusList = [];

  sort(sort: { key: string, value: string }): void {
    this.sortKey = sort.key;
    this.sortValue = sort.value;
    this.updateData(true);
  }

  constructor(private roomService: RoomService) {
  }

  updateData(reset: boolean = false): void {
    if (reset) {
      this.pageIndex = 1;
    }

    this.loading = true;
    this.dataSet = this.rooms;
    if (this.searchRoomStatusList.length > 0) {
      this.dataSet = this.rooms.filter((room) => this.searchRoomStatusList.indexOf(room.status.toString()) !== -1);
    }
    this.total = this.dataSet.length;

    // 排序不影响条目数量
    if (this.sortKey && this.sortValue) {
      this.dataSet = this.dataSet.sort((a, b) => (this.sortValue === 'ascend') ? (a[ this.sortKey ] > b[ this.sortKey ] ? 1 : -1)
        : (b[ this.sortKey ] > a[ this.sortKey ] ? 1 : -1));
    }

    this.dataSet = this.dataSet.slice((this.pageIndex - 1) * this.pageSize, (this.pageIndex - 1) * this.pageSize + this.pageSize);
    this.loading = false;
  }

  updateFilter(value: string[]): void {
    this.searchRoomStatusList = value;
    this.updateData(true);
  }

  showAddRoomDialog() {
    this.room = new Room();
    this.room.type = '天房';
    this.isAddRoomVisible = true;
  }

  confirmAddRoom() {
    if (!this.check()) {
      return;
    }

    this.isOkLoading = true;
    this.roomService.addRoom({'roomNo': this.room.roomNo, 'type': this.room.type} as Room).subscribe(result => {
      if (result !== undefined && result.success !== undefined && result.success) {
        this.rooms.push(result.data);
        this.calcIncomingAndSwitchValue();
        this.updateData(true);
      }

      this.isOkLoading = false;
      this.isAddRoomVisible = false;
    });
  }

  cancelAddRoom() {
    this.isAddRoomVisible = false;
  }

  show_bed_management_dialog(roomId: number, beds: Bed[]): void {
    this.isVisible = true;
    this.roomId = roomId;
    if (beds == null) {
      beds = [];
    }
    this.beds = beds;
  }

  show_room_management_dialog(room: Room) {
    this.isRoomVisible = true;
    this.room = room;
  }

  /**
   * 更改客房状态
   * @param data
   */
  clickSwitch(data: any): void {
    // 如果是有客状态点击不起作用
    if (data.status === 1) {
      return;
    }

    if (!data.statusLoading) {
      data.statusLoading = true;

      if (data.switchValue) {
        data.status = this.unused;
      } else {
        data.status = 0;
      }

      this.roomService.updateRoom({id: data.id, status: data.status} as Room).subscribe((result) => {
        if (result !== undefined && result.success !== undefined && result.success) {
          data.status = result.data.status;
          data.switchValue = !data.switchValue;
        }
        // 修改成功与否，都应关闭正在加载状态
        data.statusLoading = false;
      });
    }

  }

  check(): boolean {
    let right = true;

    if (this.room.roomNo == null || this.room.roomNo.toString().trim().length === 0) {
      this.message['roomNo'] = '客房号不能为空';
      right = false;
    }

    if (this.room.type == null || this.room.type.toString().trim().length === 0) {
      this.message['type'] = '客房类型不能为空';
      right = false;
    }

    return right;
  }

  ngOnInit() {
    this.loading = true;
    this.roomService.getRooms().subscribe((rooms) => {
      this.rooms = rooms;
      this.calcIncomingAndSwitchValue();
      this.updateData();
    });
  }

  calcIncomingAndSwitchValue() {
    this.rooms.forEach((room) => {
      const incomes = room.incomes;
      let sum = 0;
      if (incomes !== undefined && incomes != null) {
        incomes.forEach((income) => sum += Number.parseFloat(income.incoming));
      }

      room.sumIncoming = sum.toFixed(2);

      room.switchValue = room.status !== this.unused;

      room.statusLoading = false;
      return room;
    });
  }
}
