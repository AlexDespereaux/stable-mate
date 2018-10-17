import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Image } from '../../../_models/image';
import * as $ from 'jquery';
import * as jspdf from 'jspdf';
import html2canvas from 'html2canvas';
import domtoimage from 'dom-to-image';
import { saveAs } from 'file-saver/FileSaver';
import { ImageService } from '../../../image.service';
@Component({
  selector: 'app-display-image',
  templateUrl: './display-image.component.html',
  styleUrls: ['./display-image.component.css']
})
export class DisplayImageComponent implements OnInit {

  // has to be initialized when getting all for one image.
  image = new Image();
  imageUrl = 'assets/images/img2.jpg';
  imageName = 'test image';
  rate = 0;
  getCanvas;
  // dummy obj
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
    "legend": [
      { "name": "black_radio", "text": "cell wall" },
      { "name": "grey_star", "text": "nucleus" },
      { "name": "black_radio", "text": "cell wall" },
      { "name": "grey_star", "text": "nucleus" }
    ],
    "review": 0
  };
  legendImages = [];

  constructor(private route: ActivatedRoute, private router: Router, private imageService: ImageService) { }

  ngOnInit() {

    this.route.params.subscribe(
      (params: Params) => {
        this.image.imageId = params.id;
        console.log(this.image);
      }
    );
    this.imageService.getImageData(this.image.imageId).subscribe(
      res => {
        console.log(res);

        this.imageDetails.dFov = res['dFov'];
        this.imageDetails.datetime = res['datetime'];
        this.imageDetails.description = res['description'];
        this.imageDetails.filename = res['filename'];
        this.imageDetails.imageId = res['imageId'];
        if (res['legend'] && res['legend'].length > 0) {
          console.log('legend is defined ');
          res['legend'].forEach(
            item => this.imageDetails.legend.push(item)
          );
        }
        this.imageDetails.location.latitude = res['location'].latitude;
        this.imageDetails.location.longitude = res['location'].longitude;
        this.imageDetails.ppm = res['ppm'];
        this.imageDetails.review = res['review'];
        this.imageDetails.notes = res['notes'];
      },
      err => console.log(err)


    );

    // call will be done after getting data from server
    this.filterLegend();
  }

  filterLegend() {
    this.imageDetails.legend.forEach(
      (attr) => {
        switch (attr.name.toLowerCase()) {
          case 'black_arrow': this.legendImages.push('assets/legend/black_arrow.png'); break;
          case 'black_radio': this.legendImages.push('assets/legend/black_radio.png'); break;
          case 'black_solid_arrow': this.legendImages.push('assets/legend/black_solid_arrow.png'); break;
          case 'black_star': this.legendImages.push('assets/legend/black_star.png'); break;
          case 'grey_arrow': this.legendImages.push('assets/legend/grey_arrow.png'); break;
          case 'grey_radio': this.legendImages.push('assets/legend/grey_radio.png'); break;
          case 'grey_solid_arrow': this.legendImages.push('assets/legend/grey_solid_arrow.png'); break;
          case 'grey_star': this.legendImages.push('assets/legend/grey_star.png'); break;
          case 'white_arrow': this.legendImages.push('assets/legend/white_arrow.png'); break;
          case 'white_radio': this.legendImages.push('assets/legend/white_radio.png'); break;
          case 'white_solid_arrow': this.legendImages.push('assets/legend/white_solid_arrow.png'); break;
          case 'white_star': this.legendImages.push('assets/legend/white_star.png'); break;
        }
      }
    );
  }

  changeRating(rating) {
    this.rate = rating;
  }

  download() {


    // var button = document.getElementById("btn-Convert-Html2Image");

    var data = document.getElementById('image');
    html2canvas(data).then(canvas => {
      // Few necessary setting options  
      var imgWidth = 208;
      var pageHeight = 295;
      var imgHeight = canvas.height * imgWidth / canvas.width;
      var heightLeft = imgHeight;
      const contentDataURL = canvas.toDataURL('image/png');
      canvas.toBlob(function (blob) {
        saveAs(blob, "image.png");
      });
      let pdf = new jspdf('p', 'mm', 'a4'); // A4 size page of PDF  
      var position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight);
    });




    var data = document.getElementById('legend');
    html2canvas(data).then(canvas => {
      // Few necessary setting options  
      var imgWidth = 208;
      var pageHeight = 295;
      var imgHeight = canvas.height * imgWidth / canvas.width;
      var heightLeft = imgHeight;

      const contentDataURL = canvas.toDataURL('image/png');
      contentDataURL.attr('download', 'legend.png');


      let pdf = new jspdf('p', 'mm', 'a4'); // A4 size page of PDF  
      var position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight);
      canvas.toBlob(function (blob) {
        saveAs(blob, "legend.png");
      });
    });

    const description = document.getElementById("descriptions").textContent;
    var file = new File([description], "description.txt", { type: "text/plain;charset=utf-8" });
    saveAs(file);
  }

  downlaodDescription() {
    const description = document.getElementById("descriptions").textContent;
    var file = new File([description], "description.txt", { type: "text/plain;charset=utf-8" });
    saveAs(file);
  }

  downloadLegend() {
    var data = document.getElementById('legend');
    html2canvas(data).then(canvas => {
      // Few necessary setting options  
      var imgWidth = 208;
      var pageHeight = 295;
      var imgHeight = canvas.height * imgWidth / canvas.width;
      var heightLeft = imgHeight;

      const contentDataURL = canvas.toDataURL('image/png');
      contentDataURL.attr('download', 'legend.png');


      let pdf = new jspdf('p', 'mm', 'a4'); // A4 size page of PDF  
      var position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight);
      canvas.toBlob(function (blob) {
        saveAs(blob, "legend.png");
      });
      // pdf.save('legened.pdf'); // Generated PDF   
    });
  }

  downloadImage() {
    var data = document.getElementById('rateImage');
    html2canvas(data).then(canvas => {
      // Few necessary setting options  
      var imgWidth = 208;
      var pageHeight = 295;
      var imgHeight = canvas.height * imgWidth / canvas.width;
      var heightLeft = imgHeight;
      const contentDataURL = canvas.toDataURL('image/png');
      canvas.toBlob(function (blob) {
        saveAs(blob, "image.png");
      });
      let pdf = new jspdf('p', 'mm', 'a4'); // A4 size page of PDF  
      var position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight);
    });

   }

  onSave() {
    // this.imageService.saveImage(this.imageDetails.review).subscribe();
  }
}
