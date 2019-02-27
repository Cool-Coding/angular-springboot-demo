import { Injectable } from '@angular/core';
import { Room} from './dto/Room';
import {Observable, of} from 'rxjs';
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
export class RoomService {
  private roomsUrl = 'http://3s.dkys.org:20750/api/rooms';


  constructor(
    private http: HttpClient,
    private message: NzMessageService) { }


  /**
   * 查询所有的客房信息
   */
  getRooms(): Observable<Room[]> {
    // TODO: send the message _after_ fetching the rooms
    return this.http.get<Room[]>(this.roomsUrl).pipe(
      tap(_ => this.success(MESSAGETEXTS.FETCH_SUCCESS)),
      catchError(this.handleError<Room[]>('查询所有客房信息', []))
    );
  }

  /**
   * 查询某个客房的信息
   * @param id  客房的ID
   */
  getRoom(id: number): Observable<Room> {
    const url = `${this.roomsUrl}/${id}`;
    return this.http.get<Room>(url).pipe(
      tap(() => this.success(MESSAGETEXTS.FETCH_SUCCESS + `:id=${id}`)),
      catchError(this.handleError<Room>('查询客房信息'))
    );
  }


  /**
   * 更新客房信息
   * @param Room
   */
  updateRoom(room: Room): Observable<any> {
    return this.http.put(this.roomsUrl, room, httpOptions).pipe(
      tap(() => this.success(MESSAGETEXTS.UPDATE_SUCCESS)),
      catchError(this.handleError<any>('更新客房信息'))
    );
  }

  /**
   * 登记：创建客房信息
   * @param room   客房
   */
  addRoom (room: Room): Observable<Result> {
    return this.http.post<Result>(this.roomsUrl, room, httpOptions).pipe(
      tap((result) => {
        if (result.success) {
          this.success(result.message);
        } else {
          this.error(result.message);
        }
      }),
      catchError(this.handleError<Result>('新增客房'))
    );
  }

  /**
   * 删除客房
   * @param room  客房
   */
  deleteRoom(room: Room | number): Observable<Room> {
    let id;
    if (room instanceof Room) {
      id = room.id;
    } else if (typeof Room === 'number') {
      id =  room;
    }

    const url = `${this.roomsUrl}/${id}`;

    return this.http.delete<Room>(url, httpOptions).pipe(
      tap(() => this.success(MESSAGETEXTS.DELETE_SUCCESS)),
      catchError(this.handleError<Room>('删除客房信息'))
    );
  }


  /**
   * 搜索客房
   * @param term
   */
  searchRooms(conditions: SearchCondition[]): Observable<Room[]> {
    if (conditions === null
      || (conditions.length === 1 &&  conditions[0].key === undefined)
      || (conditions.length === 1 &&  conditions[0].key === null)
      || (conditions.length === 1 &&  conditions[0].value === undefined)
      || (conditions.length === 1 &&  conditions[0].value === null)) {
      return of([]);
    };

    let parameter = '?';
    conditions.forEach((condition) => {
      parameter += condition.key + '=' + condition.value + '&';
    });

    parameter = parameter.substr(0, parameter.length - 1);

    return this.http.get<Room[]>(`${this.roomsUrl}/${parameter}`).pipe(
      // tap((rooms) => this.success(Utils.format(MESSAGETEXTS.SEARCH_SUCCESS, [rooms.length]))),
      catchError(this.handleError<Room[]>('搜索客房信息', []))
    );
  }

  /**
   * 登记
   * @param room
   */
  login(room: Room): Observable<Result> {
    if (room === null ) {
      return of(null);
    }

    const url = `${this.roomsUrl}/login`;

    return this.http.put<Result>(url, room, httpOptions).pipe(
      tap((result) => {
        if (result.success) {
          this.success(result.message);
        } else {
          this.error(result.message);
        }
      }),
      catchError(this.handleError<Result>('登记'))
    );
  }

  /**
   * 退房
   * @param roomId
   */
  logout(roomId: number): Observable<Result> {
    if (roomId === null || roomId <= 0) {
      return of(null);
    }

    const url = `${this.roomsUrl}/logout`;
    return this.http.put<Result>(url, roomId, httpOptions).pipe(
      /**tap((result) => {
        if (result.success) {
           this.success(result.message);
        } else {
          this.error(result.message);
        }
      }),*/
      catchError(this.handleError<Result>('退房'))
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
      this.error(`${operation}失败: ${msg}`);

      // Let the app keep running by returning an empty result
      return of(result as T);
    };
  }
}
