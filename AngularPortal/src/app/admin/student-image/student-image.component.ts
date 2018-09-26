import { Component, OnInit } from '@angular/core';
import { Params, ActivatedRoute, Router } from '@angular/router';
import * as $ from 'jquery';
import * as jspdf from 'jspdf';
import html2canvas from 'html2canvas';
import domtoimage from 'dom-to-image';
import { saveAs } from 'file-saver/FileSaver';

@Component({
  selector: 'app-student-image',
  templateUrl: './student-image.component.html',
  styleUrls: ['./student-image.component.css']
})
export class StudentImageComponent implements OnInit {

  // has to be initialized when getting all for one image.
  image = new Image();
  imageUrl = 'assets/images/img2.jpg';
  imageName = 'test image';
  rate = 1;
  // dummy obj
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
      { "name": "black_radio", "text": "cell wall" },
      { "name": "grey_star", "text": "nucleus" }, 
      { "name": "black_radio", "text": "cell wall" },
      { "name": "grey_star", "text": "nucleus" }
    ]
  };
  legendImages = [];

  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    this.route.params.subscribe(
      (params: Params) => {
        // this.image.imageId = params.id;
      }
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
    console.log('change rating');
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
      // pdf.save('image.pdf'); // Generated PDF   
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
      // pdf.save('legened.pdf'); // Generated PDF   
    });

    const description = document.getElementById("descriptions").textContent;
    var file = new File([description], "description.txt", { type: "text/plain;charset=utf-8" });
    saveAs(file);


  }


}
