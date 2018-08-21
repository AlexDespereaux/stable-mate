//
//  PhotoViewController.swift
//  tester
//
//  Created by jackson on 25/7/18.
//  Copyright © 2018 JACKSON ASHELY STONEY-DOBELL. All rights reserved.
//

import UIKit
import iOSPhotoEditor
class PhotoViewController: UIViewController{
    @IBOutlet weak var imageView: UIImageView!
    
    override func viewDidLoad() {
    super.viewDidLoad()
        
        if UIImagePickerController.isCameraDeviceAvailable( UIImagePickerControllerCameraDevice.front)
        {
            let Picker =  UIImagePickerController()
            Picker.delegate = self
            Picker.sourceType = .camera;
            present(Picker, animated: true, completion: nil)
        }
       
    }
    
    
  //  @IBAction func buttonPress(_ sender: Any) {
        
          //  if UIImagePickerController.isCameraDeviceAvailable( UIImagePickerControllerCameraDevice.front)
          //  {
          //      let Picker =  UIImagePickerController()
         //       Picker.delegate = self
        //        Picker.sourceType = .camera;
        //        present(Picker, animated: true, completion: nil)
   //         }
 //   }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

extension PhotoViewController: PhotoEditorDelegate {
    
    func doneEditing(image: UIImage) {
        imageView.image = image
        //Here is where the sending Jason stuff will go
    }
    
    func canceledEditing() {
        print("Canceled")
    }
}



extension PhotoViewController:  UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    func imagePickerController(_ picker: UIImagePickerController,
                               didFinishPickingMediaWithInfo info: [String : Any]) {
        
       guard let image = info[UIImagePickerControllerOriginalImage] as? UIImage else {
           picker.dismiss(animated: true, completion: nil)
            return
      }
        picker.dismiss(animated: true, completion: nil)
        
        
        let photoEditor = PhotoEditorViewController(nibName:"PhotoEditorViewController",bundle: Bundle(for: PhotoEditorViewController.self))
        photoEditor.photoEditorDelegate = self
        photoEditor.image = image
        //Colors for drawing and Text, If not set default values will be used
        //photoEditor.colors = [.red, .blue, .green]
        
        //Stickers that the user will choose from to add on the image
        for i in 0...14 {
            photoEditor.stickers.append(UIImage(named: i.description )!)
        }
        
        //To hide controls - array of enum control
        //photoEditor.hiddenControls = [.crop, .draw, .share]
        
        present(photoEditor, animated: true, completion: nil)
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        dismiss(animated: true, completion: nil)
    }
}


