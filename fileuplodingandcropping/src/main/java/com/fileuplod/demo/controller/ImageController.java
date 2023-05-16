package com.fileuplod.demo.controller;



import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fileuplod.demo.dao.ImageRepository;
import com.fileuplod.demo.model.CroppedData;

import com.fileuplod.demo.model.ImageEntity;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;


@Controller
public class ImageController {
	@Autowired
    private ImageRepository imageRepository;

    @GetMapping("/uploads")
    public String showUploadForm(Model model) {
        model.addAttribute("croppedData", new CroppedData());
        return "hello";
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file,
                              @ModelAttribute("croppedData") CroppedData croppedData) {
        try {
            byte[] imageData = file.getBytes();
            byte[] croppedImage = cropAndRotateImage(imageData, croppedData.getX(), croppedData.getY(),
                    croppedData.getWidth(), croppedData.getHeight(), croppedData.getRotate());

            ImageEntity image = new ImageEntity();
            image.setFilename(croppedData.getName());
            image.setData(croppedImage);

            imageRepository.save(image);

            return "success"; // Redirect to a success page
        } catch (Exception e) {
            return "redirect:/error"; // Redirect to an error page
        }
    }

    private byte[] cropAndRotateImage(byte[] imageData, double x, double y, double width, double height, int rotate) {
        // Implement the image cropping and rotation logic using a library of your choice
        // Return the cropped and rotated image data as a byte array
    	  try {
    	        // Read the input image data
    	        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
    	        BufferedImage image = ImageIO.read(inputStream);

    	        // Perform cropping
    	        BufferedImage croppedImage = image.getSubimage((int) x, (int) y, (int)width,(int) height);

    	        // Perform rotation
    	        AffineTransform transform = new AffineTransform();
    	        transform.rotate(Math.toRadians(rotate), croppedImage.getWidth() / 2.0, croppedImage.getHeight() / 2.0);
    	        BufferedImage rotatedImage = new BufferedImage(croppedImage.getWidth(), croppedImage.getHeight(), croppedImage.getType());
    	        Graphics2D g = rotatedImage.createGraphics();
    	        g.setTransform(transform);
    	        g.drawImage(croppedImage, 0, 0, null);
    	        g.dispose();

    	        // Write the cropped and rotated image to a byte array
    	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	        ImageIO.write(rotatedImage, "png", outputStream);
    	        return outputStream.toByteArray();
    	    } catch (IOException e) {
    	        // Handle any errors that may occur during image processing
    	        e.printStackTrace();
    	        return null;
    	    }
    }
}

