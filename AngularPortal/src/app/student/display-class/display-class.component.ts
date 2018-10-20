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
  images = [{ id: '', image: '' }];
  class;
  searchImage = '';
  imageToShow;

  constructor(private route: ActivatedRoute, private router: Router, private imageService: ImageService) { }

  ngOnInit() {
    this.imageService.getImageList().subscribe(
      (res: any) => {
        res.forEach((re, i) => {
          this.images[i].id = re;

          this.imageService.getImage(re).subscribe(
            (res) => { },
            (err) => {
              this.images[i].image = err.url;
            }
          );
        });
      }
    );
  }


  createImageFromBlob(image) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.imageToShow = reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }
  redirect(image) {
    this.router.navigate(['image', image.id], { relativeTo: this.route });
  }

}
