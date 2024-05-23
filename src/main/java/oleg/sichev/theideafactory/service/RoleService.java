package oleg.sichev.theideafactory.service;

import oleg.sichev.theideafactory.entity.Role;
import oleg.sichev.theideafactory.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public void save(Role role) {
        roleRepository.save(role);
    }

    public Role findById(Integer id) {
        return roleRepository.findById(id).orElse(null);
    }
}