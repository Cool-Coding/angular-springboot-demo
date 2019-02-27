export class Utils {
  /**
   * 定义原生使用占位符的方法，格式化数据
   * @author sky
   * @date 2018-07-09
   * @returns string
   */
  public static format = function (str, values): string {
    // 数据长度为空，则直接返回
    if (values.length === 0) {
      return str;
    }

    // 使用正则表达式，循环替换占位符数据
    let result = str;
    for (let i = 0; i < values.length; i++) {
      result = result.replace(new RegExp('\\{' + i + '\\}', 'g'), values[i]);
      return result;
    }
  };

  /**
   * 时间格式化处理
   * @param fmt 格式
   * @param date 日期
   */
  public static dateFormat = function (date, fmt: string = 'yyyy-MM-dd hh:mm:ss'): string {
    const o = {
      'M+' : date.getMonth() + 1,                 // 月份
      'd+' : date.getDate(),                    // 日
      'h+' : date.getHours(),                   // 小时
      's+' : date.getSeconds(),                 // 秒
      'm+' : date.getMinutes(),                 // 分
      'q+' : Math.floor((date.getMonth() + 3) / 3), // 季度
      'S'  : date.getMilliseconds()             // 毫秒
    };

    if (/(y+)/.test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
    }

    for (const k in o) {
      if (new RegExp('(' + k + ')').test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)));
      }
    }
    return fmt;
  };


  // 字符串转日期格式，strDate要转为日期格式的字符串
  public static getDate(strDate): Date {
    const st = strDate;
    let a = st.split(' ');
    let b = a[0].split('-');
    let c = a[1].split(':');
    const date = new Date(b[0], b[1] - 1, b[2], c[0], c[1], c[2]);
    return date;
  }

  public static isString(str): boolean {
    return (typeof str == 'string') && str.constructor === String;
  }
}
