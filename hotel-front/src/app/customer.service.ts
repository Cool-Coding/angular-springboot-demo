import { Injectable } from '@angular/core';
import { Customer} from './dto/Customer';
import { Observable, of } from 'rxjs';
import { NzMessageService} from 'ng-zorro-antd';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { catchError, tap} from 'rxjs/operators';
import {MESSAGETEXTS} from './const/MessageConsts';
import {SearchCondition} from './dto/SearchCondition';
import {Result} from './dto/Result';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private customersUrl = 'http://3s.dkys.org:20750/api/customers';


  constructor(
    private http: HttpClient,
    private message: NzMessageService) { }


  /**
   * 查询所有的客户信息
   */
  getCustomers(): Observable<Customer[]> {
       // TODO: send the message _after_ fetching the customers
      return this.http.get<Customer[]>(this.customersUrl).pipe(
        tap(_ => this.success(MESSAGETEXTS.FETCH_SUCCESS)),
        catchError(this.handleError<Customer[]>('查询所有客人信息', []))
      );
    }

  /**
   * 查询某个客户的信息
   * @param id  客户的ID
   */
  getCustomer(id: number): Observable<Customer> {
    const url = `${this.customersUrl}/${id}`;
    return this.http.get<Customer>(url).pipe(
      tap(() => this.success(MESSAGETEXTS.FETCH_SUCCESS + `:id=${id}`)),
      catchError(this.handleError<Customer>('查询客人信息'))
    );
  }


  /**
   * 更新客户信息
   * @param customer
   */
  updateCustomer(customer: Customer): Observable<any> {
    return this.http.put(this.customersUrl, customer, httpOptions).pipe(
      tap(() => this.success(MESSAGETEXTS.UPDATE_SUCCESS)),
      catchError(this.handleError<any>('更新客人信息'))
    );
  }

  /**
   * 登记：创建客户信息
   * @param customer   客户
   */
  addCustomer (customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(this.customersUrl, customer, httpOptions).pipe(
      tap(() => this.success(MESSAGETEXTS.LOGIN_SUCCESS)),
      catchError(this.handleError<Customer>('新增客人信息'))
    );
  }

  /**
   * 删除客户
   * @param customer  客户
   */
  deleteCustomer(customer: Customer | number): Observable<Result> {
    let id;
    if (customer instanceof Customer) {
      id = customer.id;
    } else if (typeof customer === 'number') {
       id =  customer;
    }

    const url = `${this.customersUrl}/${id}`;

    return this.http.delete<Result>(url, httpOptions).pipe(
      tap((result) => {
        if (result.success) {
          this.success(result.message);
        } else {
          this.error(result.message);
        }
      }),
      catchError(this.handleError<Result>('删除客人信息'))
    );
  }

  /**
   * 搜索客户
   * @param searchConditions
   */
  searchCustomers(searchConditions: SearchCondition[]): Observable<Result> {
    if (searchConditions === null
      || (searchConditions.length === 1 &&  searchConditions[0].key === undefined)
      || (searchConditions.length === 1 &&  searchConditions[0].key === null)
      || (searchConditions.length === 1 &&  searchConditions[0].value === undefined)
      || (searchConditions.length === 1 &&  searchConditions[0].value === null)
      || (searchConditions.length === 1 && !searchConditions[0].value.trim())) {
      return of(null);
    }

    let httpParams = new HttpParams();
    searchConditions.forEach(condition => {
      httpParams = httpParams.append(condition.key, condition.value);
    });

    return this.http.get<Result>(`${this.customersUrl}`, {params: httpParams}).pipe(
      tap((result) => {
        if (result.success) {
          this.success(result.message);
        } else {
          this.error(result.message);
        }
      }),
      catchError(this.handleError<Result>('搜索客人信息'))
    );
  }


  private success(message: string) {
    this.message.create('success', message);
  }

  private error(message: string) {
    this.message.create('error', message);
  }

  private handleError<T> (operation = 'operation', result?: T) {

    return (error: any): Observable<T> => {
      let msg = error.message;
      if ( error.error.code !== 'undefined' && (typeof error.error.message === 'string' && error.error.message.constructor === String)) {
        msg = error.error.message;
      }

      // TODO: send the error to remote logging infrastructure
      // console.error(error);

      // TODO: better job of transforming error for user consumption
      this.error(`${operation} 失败: ${msg}`);

      // Let the app keep running by returning an empty result
      return of(result as T);
    };
  }
}
