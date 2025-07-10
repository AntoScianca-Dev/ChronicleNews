package it.aulab.progetto_finale_sciancalepore.services;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import it.aulab.progetto_finale_sciancalepore.dtos.ArticleDto;
import it.aulab.progetto_finale_sciancalepore.models.Article;
import it.aulab.progetto_finale_sciancalepore.models.Category;
import it.aulab.progetto_finale_sciancalepore.models.Image;
import it.aulab.progetto_finale_sciancalepore.models.User;
import it.aulab.progetto_finale_sciancalepore.repositories.ArticleRepository;
import it.aulab.progetto_finale_sciancalepore.repositories.UserRepository;

@Service
public class ArticleService implements CrudService<ArticleDto, Article, Long> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    public List<ArticleDto> readAll() {
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        Collections.sort(dtos, Comparator.comparing(ArticleDto::getPublishDate).reversed());
        return dtos;
    }

    @Override
    public ArticleDto read(Long key) {
        Optional<Article> optArticle = articleRepository.findById(key);
        if(optArticle.isPresent()) {
            return modelMapper.map(optArticle.get(), ArticleDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Articolo con id non trovato");
        }
    }

    @Override
    public ArticleDto create(Article article, Principal principal, MultipartFile[] files) {

        String url = "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = (userRepository.findById(userDetails.getId())).get();
            article.setUser(user);
        }


        article.setPublishDate(LocalDate.now());
        article.setIsAccepted(null);

        ArticleDto dto = modelMapper.map(articleRepository.save(article), ArticleDto.class);

        if (files.length > 0) {
            for (MultipartFile file : files) {
                if(!file.isEmpty()) {
                    try {
                        CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(file);
                        url = futureUrl.get();
                        imageService.saveImageOnDB(url, article);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return dto;
    }

    @Override
    public ArticleDto update(Long key, Article updateArticle, MultipartFile[] files) {
        String url="";

        if (articleRepository.existsById(key)) {
            updateArticle.setId(key);
            Article article = articleRepository.findById(key).get();
            updateArticle.setUser(article.getUser());
            boolean filesIsEmpty = Arrays.stream(files).anyMatch(file -> file.isEmpty());
            // controllo la presenza dell'immagine
            if (!filesIsEmpty) {
                try {
                    // Elimino le immagini salvate
                    if (article.getImages().size()>0) {
                        List<Image> images = article.getImages();
                        for (Image image : images) {
                            imageService.deleteImage(image.getPath());
                        }
                    }
                    // Salvataggio nuove immagini
                    for (MultipartFile file : files) {
                        try {
                            CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(file);
                            url = futureUrl.get();
                            imageService.saveImageOnDB(url, article);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // Salvataggio articolo da mandare in revisione
                    updateArticle.setIsAccepted(null);
                    return modelMapper.map(articleRepository.save(updateArticle), ArticleDto.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Se l'articolo aveva le immagini le recupero dal database e le salvo sull'articolo aggiornato
                if (article.getImages().size()>0) {
                    List<Image> images = article.getImages();
                    updateArticle.setImages(images);
                }

                // Controllo se ci sono pari dell'articolo
                if(updateArticle.equals(article) == false) {
                    updateArticle.setIsAccepted(null);
                } else {
                    updateArticle.setIsAccepted(article.getIsAccepted());
                }
                return modelMapper.map(articleRepository.save(updateArticle), ArticleDto.class);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @Override
    public void delete(Long key) {
        if (articleRepository.existsById(key)) {
            Article article = articleRepository.findById(key).get();

            try {
                List<Image> images = article.getImages();
                for (Image image : images) {
                    image.setArticle(null);
                    imageService.deleteImage(image.getPath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            articleRepository.deleteById(key);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    
    // funzione di ricerca articolo per categoria
    public List<ArticleDto> searchByCategory(Category category) {
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for (Article article : articleRepository.findByCategory(category)) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        List<ArticleDto> acceptedDtos = dtos.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
        return acceptedDtos;
    }

    // funzione di ricerca articolo per autore
    public List<ArticleDto> searchByAuthor(User user) {
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for (Article article : articleRepository.findByUser(user)) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        List<ArticleDto> acceptedDtos = dtos.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
        return acceptedDtos;
    }

    // funzione per ricerca articolo per parola
    public List<ArticleDto> search(String keyword) {
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for (Article article : articleRepository.search(keyword)) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        List<ArticleDto> acceptedDtos = dtos.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
        return acceptedDtos;
    }

    // funzione per filtrare gli articoli da revisionare
    public List<ArticleDto> articlesToReview() {
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for (Article article : articleRepository.findByIsAcceptedNull()) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }

    // funzione per filtrare gli articoli rifiutati dal revisore
    public List<ArticleDto> articlesRejected() {
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for (Article article : articleRepository.findByIsAcceptedFalse()) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }

    // funzione per filtrare gli articoli accettati dal revisore
    public List<ArticleDto> articlesAccepted() {
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }

    // funzione per accettare/rifiutare un articolo
    public void setIsAccepted(Boolean result, Long id) {
        Article article = articleRepository.findById(id).get();
        article.setIsAccepted(result);
        articleRepository.save(article);
    }
}
