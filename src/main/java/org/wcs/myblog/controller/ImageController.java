package org.wcs.myblog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wcs.myblog.dto.ImageDTO;
import org.wcs.myblog.model.Image;
import org.wcs.myblog.service.ImageService;

@RestController
@RequestMapping("/images")
public class ImageController {
  private final ImageService imageService;

  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

  @GetMapping
  public ResponseEntity<List<ImageDTO>> getAllImages() {
    List<ImageDTO> imageDTOs = imageService.getAllImages();
    if (imageDTOs.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(imageDTOs);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ImageDTO> getImageById(@PathVariable Long id) {
    ImageDTO imageDTO = imageService.getImageById(id);
    if (imageDTO == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(imageDTO);
  }

  @PostMapping
  public ResponseEntity<ImageDTO> createImage(@RequestBody Image image) {
    ImageDTO savedImage = imageService.createImage(image);
    return ResponseEntity.status(201).body(savedImage);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ImageDTO> updateImage(@PathVariable Long id, @RequestBody Image imageDetails) {
    ImageDTO updatedImage = imageService.updateImage(id, imageDetails);
    if (updatedImage == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(updatedImage);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
    if (imageService.deleteImage(id)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
