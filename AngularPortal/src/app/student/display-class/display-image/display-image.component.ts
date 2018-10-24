import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import * as jspdf from 'jspdf';
import html2canvas from 'html2canvas';
import { saveAs } from 'file-saver/FileSaver';
import { ImageService } from '../../../image.service';
import {SafeUrl} from "@angular/platform-browser";
@Component({
  selector: 'app-display-image',
  templateUrl: './display-image.component.html',
  styleUrls: ['./display-image.component.css']
})

class Image {
  id: string;
  image: SafeUrl;
}

export class DisplayImageComponent implements OnInit {

  // has to be initialized when getting all for one image.
  image = new Image();
  imageUrl = 'assets/images/img2.jpg';
  imageName = 'test image';
  rate = 0;

  imageDetails = {
    "filename": "",
    "description": "",
    "notes": "",
    "datetime": null,
    "location": {
      "latitude": null,
      "longitude": null
    },
    "imageId": null,
    "dFov": null,
    "ppm": null,
    "legend": [],
    "review": 0
  };
  legendImages = [];

  constructor(private route: ActivatedRoute, private router: Router, private imageService: ImageService) { }

  ngOnInit() {

    this.route.params.subscribe(
      (params: Params) => {
        this.image.id = params.id;
      }
    );
    this.imageService.getImage(this.image.id).subscribe(
      url => { this.image.image = url },() => {} );

    this.imageService.getImageData(this.image.id).subscribe(
      res => {
        console.log(res);

        this.imageDetails.dFov = res['dFov'];
        this.imageDetails.datetime = res['datetime'];
        this.imageDetails.description = res['description'];
        this.imageDetails.filename = res['filename'];
        this.imageDetails.imageId = res['imageId'];
        if (res['legend'] && res['legend'].length > 0) {
          res['legend'].forEach(
            item => {
              if(item.name !== ' '){
                this.imageDetails.legend.push(item)
              }
            }
          );
          this.filterLegend();

        }
        this.rate = res['rating'];
        this.imageDetails.location.latitude = res['location'].latitude;
        this.imageDetails.location.longitude = res['location'].longitude;
        this.imageDetails.ppm = res['ppm'];
        this.imageDetails.review  = res['review'];
        this.imageDetails.notes = res['notes'];
      },
      err => console.log(err)


    );

    // call will be done after getting data from server
  }

  filterLegend() {
    this.imageDetails.legend.forEach(
      (attr) => {
        switch (attr.name.toLowerCase()) {
          case 'black_arrow.png': this.legendImages.push('assets/legend/black_arrow.png'); break;
          case 'black_radio.png': this.legendImages.push('assets/legend/black_radio.png'); break;
          case 'black_solid_arrow.png': this.legendImages.push('assets/legend/black_solid_arrow.png'); break;
          case 'black_star.png': this.legendImages.push('assets/legend/black_star.png'); break;
          case 'grey_arrow.png': this.legendImages.push('assets/legend/grey_arrow.png'); break;
          case 'grey_radio.png': this.legendImages.push('assets/legend/grey_radio.png'); break;
          case 'grey_solid_arrow.png': this.legendImages.push('assets/legend/grey_solid_arrow.png'); break;
          case 'grey_star.png': this.legendImages.push('assets/legend/grey_star.png'); break;
          case 'white_arrow.png': this.legendImages.push('assets/legend/white_arrow.png'); break;
          case 'white_radio.png': this.legendImages.push('assets/legend/white_radio.png'); break;
          case 'white_solid_arrow.png': this.legendImages.push('assets/legend/white_solid_arrow.png'); break;
          case 'white_star.png': this.legendImages.push('assets/legend/white_star.png'); break;
        }
      }
    );

    console.log(this.legendImages)
  }

  changeRating(rating) {
    this.rate = rating;
  }

  download() {

    let data = document.getElementById('rateImage');
    html2canvas(data).then(canvas => {
      // Few necessary setting options  
      let imgWidth = 208;
      let imgHeight = canvas.height * imgWidth / canvas.width;
      const contentDataURL = canvas.toDataURL('image/png');
      canvas.toBlob(function (blob) {
        saveAs(blob, "image.png");
      });
      let pdf = new jspdf('p', 'mm', 'a4'); // A4 size page of PDF  
      let position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight);
    });




    data = document.getElementById('legend1');
    html2canvas(data).then(canvas => {
      // Few necessary setting options  
      let imgWidth = 208;
      let imgHeight = canvas.height * imgWidth / canvas.width;
      const contentDataURL = canvas.toDataURL('image/png');
      let pdf = new jspdf('p', 'mm', 'a4'); // A4 size page of PDF  
      let position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight);
      canvas.toBlob(function (blob) {
        saveAs(blob, "legend.png");
      });
    });

    const description = document.getElementById("descriptions").textContent;
    let file = new File([description], "description.txt", { type: "text/plain;charset=utf-8" });
    saveAs(file);
  }

  downlaodDescription() {
    const description = document.getElementById("descriptions").textContent;
    let file = new File([description], "description.txt", { type: "text/plain;charset=utf-8" });
    saveAs(file);
  }

  downloadLegend() {
    let data = document.getElementById('legend1');
    html2canvas(data).then(canvas => {
      // Few necessary setting options  
      let imgWidth = 208;
      let imgHeight = canvas.height * imgWidth / canvas.width;
      const contentDataURL = canvas.toDataURL('image/png');
      let pdf = new jspdf('p', 'mm', 'a4'); // A4 size page of PDF  
      let position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight);
      canvas.toBlob(function (blob) {
        saveAs(blob, "legend.png");
      });
    });
  }

  downloadImage() {


    let data = document.getElementById('rateImage');
    const image = "data:image/jpg;base64," + this.image.image;
    html2canvas(image).then(canvas => {
      // Few necessary setting options  
      let imgWidth = 208;
      let imgHeight = canvas.height * imgWidth / canvas.width;
      const contentDataURL = canvas.toDataURL('image/png');
      canvas.toBlob(function (blob) {
        saveAs(blob, "image.png");
      });
      let pdf = new jspdf('p', 'mm', 'a4'); // A4 size page of PDF  
      let position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight);
    });

  }

  onSave() {
    this.imageService.saveImage(this.image.id, this.rate).subscribe(
      res => console.log(res),
      err => console.log(err)
    );
  }
}
