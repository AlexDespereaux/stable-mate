import { Component, OnInit } from '@angular/core';
import { Router, NavigationExtras, ActivatedRoute } from '@angular/router';
import { PagerService } from '../pager.service';
import { ImageService } from '../image.service';

@Component({
    selector: 'app-student',
    templateUrl: './student.component.html',
    styleUrls: ['./student.component.css']
})
export class StudentComponent implements OnInit {
    // array of all items to be paged
    allItems: any[];

    // pager object
    pager: any = {};

    // paged items
    pagedItems: any[];
    // will be initialized later
    student = {
        id: '12458764',
        classes: ['Class 1', 'Class 2']
    };
    studentClass = "";
    image = 'assets/folder.jpg';
    studentId;

    constructor(private route: ActivatedRoute,
        private router: Router,
        private pagerService: PagerService,
        private imageService: ImageService) { }

    ngOnInit() {

        this.route.params.subscribe(
            
            id => this.studentId = id
                
        );
        this.allItems = this.student.classes;

        // initialize to page 1
        this.setPage(1);
    }


    setPage(page: number) {
        // get pager object from service
        this.pager = this.pagerService.getPager(this.allItems.length, page, 4);

        // get current page of items
        this.pagedItems = this.allItems.slice(this.pager.startIndex, this.pager.endIndex + 1);
    }


    redirect(classId: number) {
        this.router.navigate(['displayClass', classId], { relativeTo: this.route });
    }

}
