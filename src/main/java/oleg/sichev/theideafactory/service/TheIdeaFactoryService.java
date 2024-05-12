package oleg.sichev.theideafactory.service;
import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import oleg.sichev.theideafactory.repository.TheIdeaFactoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TheIdeaFactoryService {
    @Autowired
    private TheIdeaFactoryRepository theIdeaFactoryRepository;

    public List<TheIdeaFactoryEntity> findAll() {
        return theIdeaFactoryRepository.findAll();
    }

    public TheIdeaFactoryEntity save(TheIdeaFactoryEntity theIdeaFactoryEntity) {
        return theIdeaFactoryRepository.save(theIdeaFactoryEntity);
    }
}