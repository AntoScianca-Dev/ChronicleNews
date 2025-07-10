package it.aulab.progetto_finale_sciancalepore.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_sciancalepore.dtos.ArticleDto;
import it.aulab.progetto_finale_sciancalepore.dtos.CategoryDto;
import it.aulab.progetto_finale_sciancalepore.models.Category;
import it.aulab.progetto_finale_sciancalepore.services.ArticleService;
import it.aulab.progetto_finale_sciancalepore.services.CategoryService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ModelMapper modelMapper;

    // Rotta per la ricerca degli articoli per categoria
    @GetMapping("/search/{id}")
    public String categorySearch(@PathVariable("id") Long id, Model viewModel) {
        CategoryDto category = categoryService.read(id);
        List<ArticleDto> articles = articleService.searchByCategory(modelMapper.map(category, Category.class));
        viewModel.addAttribute("title", "Articoli della categoria " + category.getName());
        viewModel.addAttribute("articles", articles);
        return "article/index";
    }

    // Rotta per la creazione di una categoria
    @GetMapping("/create")
    public String categoryCreate(Model viewModel) {
        viewModel.addAttribute("category", new Category());
        return "category/create";
    }

    // Rotta per la memorizzazione di una categoria
    @PostMapping
    public String categoryStore(@Valid @ModelAttribute("category") Category category, BindingResult result, RedirectAttributes redirectAttributes, Model viewModel) {
        if (result.hasErrors()) {
            viewModel.addAttribute("category", category);
            return "category/create";
        }

        categoryService.create(category, null, null);
        redirectAttributes.addFlashAttribute("successMessage", "Categoria aggiunta con sucesso");
        return "redirect:/admin/dashboard";
    }

    // Rotta per la modifica di una categoria
    @GetMapping("/edit/{id}")
    public String categoryEdit(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("category", categoryService.read(id));
        return "category/update";
    }

    // Rotta per la memorizzazione delle modifiche di una categoria
    @PostMapping("/update/{id}")
    public String categoryUpdate(@PathVariable("id") Long id, @Valid @ModelAttribute("category") Category category, BindingResult result, RedirectAttributes redirectAttributes, Model viewModel) {
        if (result.hasErrors()) {
            viewModel.addAttribute("category", category);
            return "category/update";
        }

        categoryService.update(id, category, null);
        redirectAttributes.addFlashAttribute("successMessage", "Categoria modificata con successo!");
        return "redirect:/admin/dashboard";
    }

    // Rotta per la cancellazione di una categoria
    @GetMapping("/delete/{id}")
    public String categoryDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Categoria cancellata con successo!");
        return "redirect:/admin/dashboard";
    }
}
