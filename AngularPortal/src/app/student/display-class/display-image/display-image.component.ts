import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Image } from '../../../_models/image';

@Component({
  selector: 'app-display-image',
  templateUrl: './display-image.component.html',
  styleUrls: ['./display-image.component.css']
})
export class DisplayImageComponent implements OnInit {

  // has to be initialized when getting all for one image.
  image = new Image();
  imageUrl  = 'assets/images/img2.jpg';
  imageName = 'test image';
  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    this.route.params.subscribe(
      (params: Params) => {
        this.image.imageId = params.id;
      }
    );
  }

}
  