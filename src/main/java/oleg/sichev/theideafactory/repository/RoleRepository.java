package oleg.sichev.theideafactory.repository;

import oleg.sichev.theideafactory.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}