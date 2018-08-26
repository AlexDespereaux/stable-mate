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
  rate = 1;
  imageDetails = {
    "filename": "picture.png",
    "description": "apple cell with dye",
    "notes": "taken under low light",
    "datetime": 1533731244,
    "location": {
      "latitude": -37.719523,
      "longitude": 145.045910
    },
    "dFov": 1.34456,
    "ppm": 342,
    "legend": [
      { "name": "star", "text": "cell wall" },
      { "name": "triangle", "text": "nucleus" }
    ]
  };
  constructor(private route: ActivatedRoute, private router: Router) { }
 
  ngOnInit() {
    this.route.params.subscribe(
      (params: Params) => {
        this.image.imageId = params.id;
      }
    );
  }

}
  