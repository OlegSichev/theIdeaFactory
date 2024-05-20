package oleg.sichev.theideafactory.repository;

import oleg.sichev.theideafactory.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
}