package it.aulab.progetto_finale_sciancalepore.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_sciancalepore.dtos.ArticleDto;
import it.aulab.progetto_finale_sciancalepore.dtos.UserDto;
import it.aulab.progetto_finale_sciancalepore.models.User;
import it.aulab.progetto_finale_sciancalepore.repositories.CareerRequestRepository;
import it.aulab.progetto_finale_sciancalepore.services.ArticleService;
import it.aulab.progetto_finale_sciancalepore.services.CategoryService;
import it.aulab.progetto_finale_sciancalepore.services.CustomUserDetails;
import it.aulab.progetto_finale_sciancalepore.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;


@Controller
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private CategoryService categoryService;

    // Rotta di home
    @GetMapping("/")
    public String home(Model viewModel) {
        List<ArticleDto> dtos = articleService.readAll();
        List<ArticleDto> lastArticles = dtos.stream().limit(3).collect(Collectors.toList());
        viewModel.addAttribute("articles", lastArticles);
        return "home";
    }

    // Rotte di registrazione
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        
        User otherUser = userService.findUserByEmail(userDto.getEmail());
        if (otherUser != null && otherUser.getEmail() != null && !otherUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "Questa email ha già un account registrato");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "auth/register";
        }

        userService.saveUser(userDto, redirectAttributes, request, response);
        redirectAttributes.addFlashAttribute("successMessage", "Registrazione avvenuta con successo!");
        return "redirect:/";

    }

    // Rotta di login
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // Rotta per la ricerca degli articoli in base all'utente
    @GetMapping("/search/{id}")
    public String userArticlesSearch(@PathVariable("id") Long id, Model viewModel) {
        User user = userService.find(id);
        viewModel.addAttribute("title", "Articoli dell'utente " + user.getUsername());
        List<ArticleDto> articles = articleService.searchByAuthor(user);
        viewModel.addAttribute("articles", articles);
        return "article/index";
    }

    // Rotta per la dashboard dell'admin
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Richieste ricevute");
        viewModel.addAttribute("requests", careerRequestRepository.findByIsCheckedFalse());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "admin/dashboard";
    }

    // Rotta per la dashboard del revisor
    @GetMapping("/revisor/dashboard")
    public String revisorDashboard(Model viewModel){
        viewModel.addAttribute("title", "Articoli da revisionare");
        viewModel.addAttribute("articles", articleService.articlesToReview());
        viewModel.addAttribute("titleA", "Articoli accettati");
        viewModel.addAttribute("articlesA", articleService.articlesAccepted());
        viewModel.addAttribute("titleR", "Articoli rifiutati");
        viewModel.addAttribute("articlesR", articleService.articlesRejected());        
        return "revisor/dashboard";
    }

    // Rotta per la dashboard del writer
    @GetMapping("/writer/dashboard")
    public String writerDashboard(Model viewModel) {
        User user;
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userService.find(userDetails.getId());
        viewModel.addAttribute("title", "I tuoi articoli");
        viewModel.addAttribute("articles", articleService.searchByAuthor(user));
        return "writer/dashboard";
    }
    
}
