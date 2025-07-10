package it.aulab.progetto_finale_sciancalepore.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_sciancalepore.dtos.ArticleDto;
import it.aulab.progetto_finale_sciancalepore.dtos.CategoryDto;
import it.aulab.progetto_finale_sciancalepore.models.Article;
import it.aulab.progetto_finale_sciancalepore.models.Category;
import it.aulab.progetto_finale_sciancalepore.services.ArticleService;
import it.aulab.progetto_finale_sciancalepore.services.CrudService;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    
    @Autowired
    @Qualifier("categoryService")
    private CrudService<CategoryDto, Category, Long> categoryService;

    // Rotta index degli articoli
    @GetMapping
    public String articlesIndex(Model viewModel) {
        List<ArticleDto> articles = articleService.readAll();
        viewModel.addAttribute("title", "Tutti gli articoli");
        viewModel.addAttribute("articles", articles);
        return "article/index";
    }

    // Rotta per il dettaglio dell'articolo
    @GetMapping("detail/{id}")
    public String articleDetail(@PathVariable("id") Long id ,Model viewModel) {
        viewModel.addAttribute("article", articleService.read(id));
        return "article/detail";
    }

    // Rotta di modifica di un articolo
    @GetMapping("/edit/{id}")
    public String articleEdit(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("article", articleService.read(id));
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/edit";
    }

    // Rotta di salvataggio update di un articolo
    @PostMapping("/update/{id}")
    public String articleUpdate(@PathVariable("id") Long id, @Valid @ModelAttribute("article") Article article, BindingResult result, RedirectAttributes redirectAttributes, Principal principal, MultipartFile[] files, Model viewModel) {

        if (result.hasErrors()) {
            article.setImages(articleService.read(id).getImages());
            viewModel.addAttribute("article", articleService.read(id));
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/edit";
        }

        articleService.update(id, article, files);
        
        redirectAttributes.addFlashAttribute("successMessage", "Articolo modificato con successo! Ora è in attesa di essere approvato dal revisore.");

        return "redirect:/writer/dashboard";
    }

    // Rotta per la cancellazione di un articolo
    @GetMapping("/delete/{id}")
    public String articleDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo cancellato con successo!");

        return "redirect:/writer/dashboard";
    }
    

    // Rotta di dettaglio dell'articolo per i revisor
    @GetMapping("revisor/detail/{id}")
    public String revisorDetailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("article", articleService.read(id));
        return "revisor/detail";
    }

    // Rotta per accettare/rifiutare un articolo
    @PostMapping("/accept")
    public String articleSetAccepted(@RequestParam("action") String action, @RequestParam("articleId") Long articleId, RedirectAttributes redirectAttributes) {
        if (action.equals("accept")) {
            articleService.setIsAccepted(true, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Articolo accettato!");
        } else if (action.equals("reject")) {
            articleService.setIsAccepted(false, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Articolo rifiutato!");
        } else {
            redirectAttributes.addFlashAttribute("resultMessage", "Azione non corretta!");
        }

        return "redirect:/revisor/dashboard";
    }

    // Rotta per la creazione dell'articolo
    @GetMapping("/create")
    public String articleCreate(Model viewModel) {
        viewModel.addAttribute("article", new Article()); 
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/create";
    }

    // Rotta per il salvataggio di un articolo
    @PostMapping
    public String articleStore(@Valid @ModelAttribute("article") Article article, BindingResult result, RedirectAttributes redirectAttributes, Principal principal, MultipartFile[] files, Model viewModel) {
        
        if(result.hasErrors()) {
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/create";
        }

        articleService.create(article, principal, files);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo aggiunto con successo!");

        return "redirect:/";
    }

    // Rotta di ricerca di un articolo
    @GetMapping("/search")
    public String articleSearch(@Param("keyword") String keyword, Model viewModel) {
        List<ArticleDto> articles = articleService.search(keyword);
        viewModel.addAttribute("title", "Parola ricercata: " + keyword);
        viewModel.addAttribute("articles", articles);
        return "article/index";
    }
}
