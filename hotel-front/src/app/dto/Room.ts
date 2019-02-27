import {Bed} from './Bed';
import {Customer} from './Customer';
import {Income} from './Income';
import {Charge} from './Charge';

export class Room {
  id: number;

  /**
   * 房间号
   */
  roomNo: string;


  /**
   * 类型
   */
  type: string;
  /**
   * 床
   */
  beds: Bed[];

  /**
   * 客人
   */
  customers: Customer[];
  /**
   * 当前房价
   */
  money: number;


  /**
   * 入住日期
   */
  checkInDate: string;

  /**
   * 退房日期
   */
  checkOutDate: string;

  /**
   * 客房状态(0:空房;1:有客;2:不可订)
   */
  status: number;

  /**
   * 收入
   */
  incoming: Income[];

  /**
   * 定价记录
   */
  charges: Charge[];
}
