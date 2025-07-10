package it.aulab.progetto_finale_sciancalepore.models;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="articles")
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotEmpty
    @Size(max=100)
    private String title;
    
    @Column(nullable = false, length = 100)
    @NotEmpty
    @Size(max=100)
    private String subtitle;
    
    
    @Column(nullable=false, length = 1000)
    @NotEmpty
    @Size(max = 1000)
    private String body;
    
    @Column(nullable = true, length = 8)
    @NotNull
    private LocalDate publishDate;

    @Column(nullable = true)
    private Boolean isAccepted;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"articles"})
    private User user;

    @ManyToOne
    @JsonIgnoreProperties({"articles"})
    private Category category;

    @OneToMany(mappedBy = "article")
    @JsonIgnoreProperties({"article"})
    private List<Image> images;

    @Override
    public boolean equals(Object obj) {
        Article article = (Article) obj;

        if(title.equals(article.getTitle()) && 
            subtitle.equals(article.getSubtitle()) &&
            body.equals(article.getBody()) && 
            publishDate.equals(article.getPublishDate()) && 
            category.getName().equals(article.getCategory().getName())) {
                return true;
            }

        return false;
    }
}
