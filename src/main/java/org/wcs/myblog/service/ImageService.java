package org.wcs.myblog.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.wcs.myblog.dto.ImageDTO;
import org.wcs.myblog.mapper.ImageMapper;
import org.wcs.myblog.model.Image;
import org.wcs.myblog.repository.ArticleRepository;
import org.wcs.myblog.repository.ImageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final ArticleRepository articleRepository;
    private final ImageMapper imageMapper;

    public ImageService(ImageRepository imageRepository, ArticleRepository articleRepository, ImageMapper imageMapper) {
        this.imageRepository = imageRepository;
        this.articleRepository = articleRepository;
        this.imageMapper = imageMapper;
    }

    public List<ImageDTO> getAllImages() {
        List<Image> images = imageRepository.findAll();
        return images.stream()
                .map(imageMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    public ImageDTO getImageById(Long id) {
        Image image = imageRepository.findById(id).orElse(null);
        return imageMapper.convertToDTO(image);
    }

    public ImageDTO createImage(Image image) {
        Image savedImage = imageRepository.save(image);
        return imageMapper.convertToDTO(savedImage);
    }

    public ImageDTO updateImage(Long id, Image imageDetails) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image == null) {
            return null;
        }
        image.setUrl(imageDetails.getUrl());
        Image updatedImage = imageRepository.save(image);
        return imageMapper.convertToDTO(updatedImage);
    }
    public boolean deleteImage(Long id) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image == null) {
            return false;
        }
        imageRepository.delete(image);
        return true;
    }
}
