package oleg.sichev.theideafactory.service;

import oleg.sichev.theideafactory.entity.Like;
import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import oleg.sichev.theideafactory.entity.User;
import oleg.sichev.theideafactory.repository.LikeRepository;
import oleg.sichev.theideafactory.repository.TheIdeaFactoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private TheIdeaFactoryRepository theIdeaFactoryRepository;

    public void toggleLike(User user, TheIdeaFactoryEntity theIdeaFactoryEntity) {
        Optional<Like> existingLike = likeRepository.findByUserAndTheIdeaFactoryEntity(user, theIdeaFactoryEntity);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setTheIdeaFactoryEntity(theIdeaFactoryEntity);
            like.setCreatedAt(LocalDateTime.now());
            likeRepository.save(like);
        }
    }

    public List<TheIdeaFactoryEntity> getLikedIdeasByUser(User user) {
        List<Like> likes = likeRepository.findAllByUser(user);

        return likes.stream()
                .map(Like::getTheIdeaFactoryEntity)
                .sorted(Comparator.comparing(TheIdeaFactoryEntity::getPostedDate).reversed())
                .collect(Collectors.toList());
    }
}
