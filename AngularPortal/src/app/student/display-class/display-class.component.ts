import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ImageService } from '../../image.service';

@Component({
  selector: 'app-display-class',
  templateUrl: './display-class.component.html',
  styleUrls: ['./display-class.component.css']
})
export class DisplayClassComponent implements OnInit {
  // this will be replaced by a service call to get images of a ceratin class 
  images = [
    { id: 1, image: '../assets/images/img1.jpg', filename: 'Image 1' },
    { id: 1, image: '../assets/images/img2.jpg', filename: 'Image 2' },
    { id: 1, image: '../assets/images/img3.jpg', filename: 'Image 3' },
    { id: 1, image: '../assets/images/img4.jpg', filename: 'Image 4' }
  ];
  class;
  searchImage = '';

  constructor(private route: ActivatedRoute, private router: Router, private imageService: ImageService) { }

  ngOnInit() {
    this.route.params.subscribe(
      params => {
        this.class = params.id;
      },
      (err) => { 
        console.log('Something went wrong');
      });

    console.log('getting images');
    this.imageService.getImageList().subscribe(
      (res) => {
        console.log(res);

        for (let i = 0; i < 4; i++) {
          this.images[i].id = res[i];
          this.imageService.getImage(res[i]).subscribe(
            (res) => {
              console.log(res);
            }
          );
        }
      }
    );
  }

  redirect(image) {
    this.router.navigate(['image', image.id], { relativeTo: this.route });
  }

}
