import { Component, OnInit } from '@angular/core';
import {logger} from "codelyzer/util/logger";

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {

  constructor() { }

  onLogin(){
     console.log('ONlcc');
  }

  ngOnInit() {
  }

}
