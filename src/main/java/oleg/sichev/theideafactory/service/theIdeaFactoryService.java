package oleg.sichev.theideafactory.service;
import oleg.sichev.theideafactory.entity.theIdeaFactoryEntity;
import oleg.sichev.theideafactory.repository.theIdeaFactoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class theIdeaFactoryService {
    @Autowired
    private theIdeaFactoryRepository theIdeaFactoryRepository;

    public List<theIdeaFactoryEntity> findAll() {
        return theIdeaFactoryRepository.findAll();
    }

    public theIdeaFactoryEntity save(theIdeaFactoryEntity theIdeaFactoryEntity) {
        return theIdeaFactoryRepository.save(theIdeaFactoryEntity);
    }
}