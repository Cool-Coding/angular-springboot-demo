import {Component, forwardRef, Input, OnInit, Output} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {Room} from '../dto/Room';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';
import {RoomService} from '../room.service';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';

@Component({
  selector: 'app-room-no-search',
  templateUrl: './room-no-search.component.html',
  styleUrls: ['./room-no-search.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: forwardRef(() => RoomNoSearchComponent),
    }
  ]
})

export class RoomNoSearchComponent implements OnInit, ControlValueAccessor {
  rooms$: Observable<Room[]>;
  private searchTerms = new Subject<string>();

  @Input() private _value: string;
  @Input() showUnused: boolean;

  constructor(private roomService: RoomService) {
    this.showUnused = true;
  }


  search(term: string): void {
    this.propagateChange(term);
    this.searchTerms.next(term);
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

  }

  get value(): string {
    return this._value;
  }

  set value(value: string) {
    this._value = value;
    this.propagateChange(this._value);
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
      this.value = value;
    }
  }

}
