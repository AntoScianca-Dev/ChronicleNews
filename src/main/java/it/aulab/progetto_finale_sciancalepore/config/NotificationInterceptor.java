package it.aulab.progetto_finale_sciancalepore.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import it.aulab.progetto_finale_sciancalepore.dtos.ArticleDto;
import it.aulab.progetto_finale_sciancalepore.models.Article;
import it.aulab.progetto_finale_sciancalepore.models.User;
import it.aulab.progetto_finale_sciancalepore.repositories.ArticleRepository;
import it.aulab.progetto_finale_sciancalepore.repositories.CareerRequestRepository;
import it.aulab.progetto_finale_sciancalepore.repositories.UserRepository;
import it.aulab.progetto_finale_sciancalepore.services.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class NotificationInterceptor implements HandlerInterceptor {
    
    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private UserRepository userRepository;
    

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && request.isUserInRole("ROLE_ADMIN")) {
            int careerCount = careerRequestRepository.findByIsCheckedFalse().size();
            modelAndView.addObject("careerRequests", careerCount);
        }

        if (modelAndView != null && request.isUserInRole("ROLE_REVISOR")) {
            int revisedCount = articleRepository.findByIsAcceptedNull().size();
            int rejectCount = articleRepository.findByIsAcceptedFalse().size();
            int acceptCount = articleRepository.findByIsAcceptedTrue().size();
            modelAndView.addObject("articlesToBeRevised", revisedCount);
            modelAndView.addObject("articlesAccepted", acceptCount);
            modelAndView.addObject("articlesRejected", rejectCount);
        }

        if (modelAndView != null && request.isUserInRole("ROLE_WRITER")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<ArticleDto> acceptedArticle = new ArrayList<>();
            List<ArticleDto> inReviewArticle = new ArrayList<>();
            List<ArticleDto> rejectArticle = new ArrayList<>();
            if (authentication != null) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                User user = (userRepository.findById(userDetails.getId())).get();
                List<ArticleDto> userArticles = new ArrayList<>();
                for (Article article : articleRepository.findByUser(user)) {
                    userArticles.add(modelMapper.map(article, ArticleDto.class));
                }
                acceptedArticle = userArticles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
                inReviewArticle = userArticles.stream().filter(article -> (article.getIsAccepted() == null)).collect(Collectors.toList());
                rejectArticle = userArticles.stream().filter(article -> Boolean.FALSE.equals(article.getIsAccepted())).collect(Collectors.toList());
            }

            int acceptedCount = acceptedArticle.size();
            int inReviewCount = inReviewArticle.size();
            int rejectCount = rejectArticle.size();
            modelAndView.addObject("articlesWritten", acceptedCount);
            modelAndView.addObject("articlesInReview", inReviewCount);
            modelAndView.addObject("articlesReject", rejectCount);
        }
    }
}
