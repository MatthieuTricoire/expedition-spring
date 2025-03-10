package org.wcs.myblog.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.wcs.myblog.dto.ArticleAuthorDTO;
import org.wcs.myblog.dto.ArticleCreateDTO;
import org.wcs.myblog.dto.ArticleDTO;
import org.wcs.myblog.dto.ImageDTO;
import org.wcs.myblog.exception.ResourceNotFoundException;
import org.wcs.myblog.mapper.ArticleMapper;
import org.wcs.myblog.mapper.ImageMapper;
import org.wcs.myblog.model.Article;
import org.wcs.myblog.model.ArticleAuthor;
import org.wcs.myblog.model.Author;
import org.wcs.myblog.model.Category;
import org.wcs.myblog.model.Image;
import org.wcs.myblog.repository.ArticleAuthorRepository;
import org.wcs.myblog.repository.ArticleRepository;
import org.wcs.myblog.repository.AuthorRepository;
import org.wcs.myblog.repository.CategoryRepository;
import org.wcs.myblog.repository.ImageRepository;

@Service
public class ArticleService {

  private final ArticleRepository articleRepository;
  private final ArticleMapper articleMapper;
  private final ImageMapper imageMapper;
  private final CategoryRepository categoryRepository;
  private final ImageRepository imageRepository;
  private final AuthorRepository authorRepository;
  private final ArticleAuthorRepository articleAuthorRepository;

  public ArticleService(
      ArticleRepository articleRepository,
      ArticleMapper articleMapper,
      ImageMapper imageMapper,
      CategoryRepository categoryRepository,
      ImageRepository imageRepository,
      AuthorRepository authorRepository,
      ArticleAuthorRepository articleAuthorRepository) {
    this.articleRepository = articleRepository;
    this.articleMapper = articleMapper;
    this.imageMapper = imageMapper;
    this.categoryRepository = categoryRepository;
    this.imageRepository = imageRepository;
    this.authorRepository = authorRepository;
    this.articleAuthorRepository = articleAuthorRepository;
  }

  public List<ArticleDTO> getAllArticles() {
    List<Article> articles = articleRepository.findAll();
    return articles.stream().map(articleMapper::convertToDTO).collect(Collectors.toList());
  }

  public List<ArticleDTO> getArticlesByTitle(String searchTerms) {
    List<Article> articles = articleRepository.findByTitle(searchTerms);
    return articles.stream().map(articleMapper::convertToDTO).collect(Collectors.toList());
  }

  public List<ArticleDTO> getArticlesByContentContaining(String searchContent) {
    List<Article> articles = articleRepository.findByContentContaining(searchContent);
    return articles.stream().map(articleMapper::convertToDTO).collect(Collectors.toList());
  }

  public List<ArticleDTO> getArticlesCreatedAfter(LocalDateTime searchDate) {
    List<Article> articles = articleRepository.findByCreatedAtAfter(searchDate);
    return articles.stream().map(articleMapper::convertToDTO).collect(Collectors.toList());
  }

  public List<ArticleDTO> getLastFiveArticlesOrdered() {
    List<Article> articles = articleRepository.findTop5ByOrderByCreatedAtDesc();
    return articles.stream().map(articleMapper::convertToDTO).collect(Collectors.toList());
  }

  public ArticleDTO getArticleById(Long id) {
    Article article = articleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("L'article avec l'id " + id + " n'a pas été trouvé"));
    return articleMapper.convertToDTO(article);
  }

  public ArticleDTO createArticle(ArticleCreateDTO articleCreateDTO) {
    Article article = articleMapper.convertToEntity(articleCreateDTO);
    article.setCreatedAt(LocalDateTime.now());
    article.setUpdatedAt(LocalDateTime.now());
    // Gestion de la catégorie
    if (articleCreateDTO.getCategoryId() != null) {
      Category category = categoryRepository.findById(articleCreateDTO.getCategoryId())
          .orElseThrow(() -> new ResourceNotFoundException(
              "La catégorie avec l'id " + articleCreateDTO.getCategoryId() + " n'a pas été trouvée"));
      article.setCategory(category);
    }

    // Gestion des images

    List<Image> validImages = new ArrayList<>();
    for (ImageDTO imageDTO : articleCreateDTO.getImages()) {
      if (imageDTO.getId() != null) {
        Image existingImage = imageRepository.findById(imageDTO.getId()).orElse(null);
        if (existingImage != null) {
          validImages.add(existingImage);
        } else {
          return null;
        }
      } else {
        Image image = imageMapper.convertToEntity(imageDTO);
        Image savedImage = imageRepository.save(image);
        validImages.add(savedImage);
      }
    }
    article.setImages(validImages);

    Article savedArticle = articleRepository.save(article);

    // Gestion des auteurs
    if (articleCreateDTO.getAuthors() != null) {
      for (ArticleAuthorDTO articleAuthorDTO : articleCreateDTO.getAuthors()) {
        Long authorId = articleAuthorDTO.getAuthorId();
        Author author = authorRepository.findById(authorId).orElse(null);
        if (author == null) {
          return null;
        }

        ArticleAuthor articleAuthor = new ArticleAuthor();
        articleAuthor.setAuthor(author);
        articleAuthor.setArticle(savedArticle);
        articleAuthor.setContribution(articleAuthorDTO.getContribution());

        articleAuthorRepository.save(articleAuthor);
      }
    }

    return articleMapper.convertToDTO(savedArticle);
  }

  public ArticleDTO updateArticle(Long id, Article articleDetails) {
    Article article = articleRepository.findById(id).orElse(null);
    if (article == null) {
      return null;
    }
    article.setTitle(articleDetails.getTitle());
    article.setContent(articleDetails.getContent());
    article.setUpdatedAt(LocalDateTime.now());

    // Mise à jour de la catégorie
    if (articleDetails.getCategory() != null) {
      Category category = categoryRepository.findById(articleDetails.getCategory().getId()).orElse(null);
      if (category == null) {
        return null;
      }
      article.setCategory(category);
    }

    // Mise à jour des images
    if (articleDetails.getImages() != null) {
      List<Image> validImages = new ArrayList<>();
      for (Image image : articleDetails.getImages()) {
        if (image.getId() != null) {
          Image existingImage = imageRepository.findById(image.getId()).orElse(null);
          if (existingImage != null) {
            validImages.add(existingImage);
          } else {
            return null;
          }
        } else {
          Image savedImage = imageRepository.save(image);
          validImages.add(savedImage);
        }
      }
      article.setImages(validImages);
    } else {
      article.getImages().clear();
    }

    // Mise à jour des auteurs
    if (articleDetails.getArticleAuthors() != null) {
      for (ArticleAuthor oldArticleAuthor : article.getArticleAuthors()) {
        articleAuthorRepository.delete(oldArticleAuthor);
      }

      List<ArticleAuthor> updatedArticleAuthors = new ArrayList<>();

      for (ArticleAuthor articleAuthorDetails : articleDetails.getArticleAuthors()) {
        Author author = articleAuthorDetails.getAuthor();
        author = authorRepository.findById(author.getId()).orElse(null);
        if (author == null) {
          return null;
        }

        ArticleAuthor newArticleAuthor = new ArticleAuthor();
        newArticleAuthor.setAuthor(author);
        newArticleAuthor.setArticle(article);
        newArticleAuthor.setContribution(articleAuthorDetails.getContribution());

        updatedArticleAuthors.add(newArticleAuthor);
      }

      for (ArticleAuthor articleAuthor : updatedArticleAuthors) {
        articleAuthorRepository.save(articleAuthor);
      }

      article.setArticleAuthors(updatedArticleAuthors);
    }

    Article updatedArticle = articleRepository.save(article);
    return articleMapper.convertToDTO(updatedArticle);
  }

  public boolean deleteArticle(Long id) {
    Article article = articleRepository.findById(id).orElse(null);
    if (article == null) {
      return false;
    }

    articleAuthorRepository.deleteAll(article.getArticleAuthors());
    articleRepository.delete(article);
    return true;
  }
}
