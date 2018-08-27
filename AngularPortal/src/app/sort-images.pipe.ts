import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'sortImages'
})
export class SortImagesPipe implements PipeTransform {

  transform(items: any[], searchText: string): any[] {
    if (!items) return [];
    if (!searchText) return items;

    searchText = searchText.toLowerCase();
console.log(items);
    return items.filter(it => {
      return it.filename.toLowerCase().includes(searchText);
    });
  }
}
