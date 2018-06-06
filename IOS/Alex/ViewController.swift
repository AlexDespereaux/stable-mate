//
//  ViewController.swift
//  Jaram iOS app
//
//  Created by Jaram on 15/5/18.
//  Copyright © 2018 Jaram. All rights reserved.
//

import UIKit


class ViewController: UIViewController {


    override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
    // Dispose of any resources that can be recreated.
    }

    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var pass: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
         /*
        let myColor = UIColor.white
        email.layer.borderColor = myColor.cgColor
        pass.layer.borderColor = myColor.cgColor
        
        email.layer.borderWidth = 1.0
        pass.layer.borderWidth = 1.0
        
        
       
        let border = CALayer()
        let width = CGFloat(2.0)
        border.borderColor = UIColor.darkGray.cgColor
        border.frame = CGRect(x: 0, y: textField.frame.size.height - width, width:  textField.frame.size.width, height: textField.frame.size.height)
        
        border.borderWidth = width
        textField.layer.addSublayer(border)
        textField.layer.masksToBounds = true
        */
    }

}
