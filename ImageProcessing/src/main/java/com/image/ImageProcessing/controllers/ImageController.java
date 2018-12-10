package com.image.ImageProcessing.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.image.ImageProcessing.response.ImagesResponse;
import com.image.ImageProcessing.service.ImageProcessorService;

@RestController
public class ImageController {

	@Autowired
	private ImageProcessorService service;
	
	@GetMapping("/images/{path}")
	public ResponseEntity<ImagesResponse> getGifImageProcessed(@PathVariable("path") String name) {
		try {
			return ResponseEntity.ok(service.divideGifImages(name));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/images")
	public ResponseEntity<List<String>> listFiles(){
		try {
			return ResponseEntity.ok().body(service.listFiles());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
