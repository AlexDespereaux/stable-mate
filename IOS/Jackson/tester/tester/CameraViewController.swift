//
//  CameraViewController.swift
//  tester
//
//  Created by JACKSON ASHELY STONEY-DOBELL on 7/6/18.
//  Copyright © 2018 JACKSON ASHELY STONEY-DOBELL. All rights reserved.
//
import UIKit
import AVFoundation
class CameraViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate {

    
    
    var imagePickerController: UIImagePickerController!
    var imageView: UIImage!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
}
  

    @IBAction func goToEditor(_ sender: Any)
    {
        
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let resultViewController = storyBoard.instantiateViewController(withIdentifier: "photoVC") as! PhotoViewController
        self.present(resultViewController, animated:true, completion:nil)
    }
    


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
    
    







