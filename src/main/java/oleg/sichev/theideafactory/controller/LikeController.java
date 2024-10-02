package oleg.sichev.theideafactory.controller;

import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import oleg.sichev.theideafactory.entity.User;
import oleg.sichev.theideafactory.service.LikeService;
import oleg.sichev.theideafactory.repository.TheIdeaFactoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LikeController {

    @Autowired
    private LikeService likeService;
    @Autowired
    private TheIdeaFactoryRepository theIdeaFactoryRepository;

    @PostMapping("/like/{postId}")
    public void addLike(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        TheIdeaFactoryEntity theIdeaFactoryEntity = theIdeaFactoryRepository.findById(postId).orElseThrow();
        likeService.toggleLike(user, theIdeaFactoryEntity);
    }

    @GetMapping("/liked-ideas")
    public List<TheIdeaFactoryEntity> getLikedIdeas(@AuthenticationPrincipal User user) {
        return likeService.getLikedIdeasByUser(user);
    }
}
