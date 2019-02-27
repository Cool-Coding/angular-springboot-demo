import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CustomerService} from '../customer.service';
import {SearchCondition} from '../dto/SearchCondition';
import {Customer} from '../dto/Customer';
import {NzMessageService} from 'ng-zorro-antd';
import {RoomService} from '../room.service';

@Component({
  selector: 'app-customer-management',
  templateUrl: './customer-management.component.html',
  styleUrls: ['./customer-management.component.css']
})
export class CustomerManagementComponent implements OnInit {
  validateForm: FormGroup;
  customers: Customer[];
  dataSet = [];
  editCache = {};

  constructor(private fb: FormBuilder,
              private customerService: CustomerService,
              private roomService: RoomService,
              private message: NzMessageService) {
    this.validateForm = this.fb.group({
      name:     [''],
      idCard:   ['', [Validators.pattern(/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/)]],
      phoneNo:  ['', [Validators.pattern(/(^[1][3,4,5,7,8][0-9]{9}$)/)]],
    });
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

  startEdit(key: string): void {
    this.editCache[ key ].edit = true;
  }

  cancelEdit(key: string): void {
    this.editCache[ key ].edit = false;
  }

  delete(key: number): void {
    const index = this.dataSet.findIndex(item => item.key === key);
    const customer = this.dataSet[index];
    customer.isSpinning = true;
    // 调用删除服务
    this.customerService.deleteCustomer(this.editCache[ key ].data.id).subscribe(result => {
      if (result !== undefined && result.success !== undefined && result.success) {
        this.dataSet = this.dataSet.filter(d => d.key !== key);
        delete this.editCache[key];
      }
      customer.isSpinning = false;
    });
  }

  saveEdit(key: number): void {

    if (!this.check(this.editCache[ key ].data)) {
      return;
    }

    // 判断有没有更新
    const index = this.dataSet.findIndex(item => item.key === key);
    const customer = this.dataSet[index];
    const editCustomer = this.editCache[ key ].data;
    if (customer.id === editCustomer.id
    && customer.name === editCustomer.name
    && customer.idCard === editCustomer.idCard
    && customer.phoneNo === editCustomer.phoneNo
    && customer.comment === editCustomer.comment) {
      this.editCache[ key ].edit = false;
      return;
    }

    // 调用修改服务
    customer.isSpinning = true;
    this.customerService.updateCustomer(this.editCache[ key ].data as Customer).subscribe(result => {
      if (result !== undefined && result.success !== undefined && result.success) {
        const index = this.dataSet.findIndex(item => item.key === key);
        Object.assign(this.dataSet[ index ], result.data);
        this.editCache[ key ].edit = false;
      }
      customer.isSpinning = false;
    });
  }

  check(data: any): boolean {
    // 姓名
    if (data.name == null || !data.name.trim()) {
      this.message.error('姓名不能为空');
      return false;
    }

    // 校验身份证号
    if (data.idCard == null || !data.idCard.trim()) {
      this.message.error('身份证号不能为空');
      return false;
    }

    let regex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
    if (!regex.test(data.idCard)) {
      this.message.error('身份证号不正确');
      return false;
    }

    // 校验手机号
    if (data.phoneNo == null || !data.phoneNo.trim()) {
      this.message.error('手机号不能为空');
      return false;
    }

    regex = /(^[1][3,4,5,7,8][0-9]{9}$)/;
    if (!regex.test(data.phoneNo)) {
      this.message.error('手机号不正确');
      return false;
    }

    return true;
  }

  resetForm(e: MouseEvent): void {
    e.preventDefault();
    this.validateForm.reset();
  }

  submitForm = ($event) => {
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

    this.customerService.searchCustomers(conditions).subscribe(result => {
      if (result !== undefined && result.success !== undefined && result.success) {
        // @ts-ignore
        this.customers = result.data;
        this.init();
        // 此处要复制，而不是引用，这样内部组件值的改变不会引响到组件外，不能使用this.dataSet = [...this.beds];，因为复制的是引用
        this.customers.forEach((item, index) => {
          this.dataSet.push({key: index, ...item});
        });
        this.updateEditCache();

      }
    });
  }

  init(): void {
    this.dataSet = [];
    this.editCache = {};
  }

  ngOnInit() {
  }

}
