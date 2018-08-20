import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'sortImages'
})
export class SortImagesPipe implements PipeTransform {

  transform(value: any, args?: any): any {
    return null;
  }

}
