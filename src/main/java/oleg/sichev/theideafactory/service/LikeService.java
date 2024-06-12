package oleg.sichev.theideafactory.service;

import oleg.sichev.theideafactory.entity.Like;
import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import oleg.sichev.theideafactory.entity.User;
import oleg.sichev.theideafactory.repository.LikeRepository;
import oleg.sichev.theideafactory.repository.TheIdeaFactoryRepository;
import oleg.sichev.theideafactory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private TheIdeaFactoryRepository entryRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean likePost(long entryId, Integer userId) {
        Optional<TheIdeaFactoryEntity> entryOptional = entryRepository.findById(entryId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (entryOptional.isPresent() && userOptional.isPresent()) {
            TheIdeaFactoryEntity entry = entryOptional.get();
            User user = userOptional.get();

            if (likeRepository.findByEntryAndUser(entry, user).isPresent()) {
                // Пользователь уже лайкнул этот пост
                return false;
            } else {
                Like like = new Like();
                like.setEntry(entry);
                like.setUser(user);
                likeRepository.save(like);
                return true;
            }
        }
        return false;
    }
}
