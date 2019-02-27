export class Charge {
  id: number;


  /**
   * 时间数量
   */
  count: number;

  /**
   * 时间单位
   */
  timeUnit: string;

  /**
   * 入住费用(单位:元)
   */
  money: number;

  /**
   * 起始日期
   */
  startDate: string;

  /**
   * 截止日期
   */
  endDate: string;
}
