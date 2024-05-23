package oleg.sichev.theideafactory.controller;

import oleg.sichev.theideafactory.entity.Role;
import oleg.sichev.theideafactory.entity.User;
import oleg.sichev.theideafactory.repository.RoleRepository;
import oleg.sichev.theideafactory.repository.UserRepository;
import oleg.sichev.theideafactory.service.RoleService;
import oleg.sichev.theideafactory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;  // Внедрение PasswordEncoder

    @GetMapping("/add_user")
    public String addUserForm(Model model) {
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        return "addUser"; // Убедитесь, что у вас есть файл templates/addUser.html
    }

    @PostMapping("/submit_user")
    public String submitUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("surname") String surname,
            @RequestParam("name") String name,
            @RequestParam(value = "middle_name", required = false) String middleName,
            @RequestParam(value = "snm", required = false) String snm,
            @RequestParam(value = "snm_in_the_genitive_case", required = false) String snmInGenitive,
            @RequestParam(value = "snm_in_the_dative_case", required = false) String snmInDative,
            @RequestParam("phone_number") String phoneNumber,
            @RequestParam(value = "work_phone_number", required = false) String workPhoneNumber,
            @RequestParam("email") String email,
            @RequestParam(value = "position_at_work", required = false) String positionAtWork,
            @RequestParam(value = "roles", required = false) List<Integer> roleIds,
            Model model) {

        // Проверка на существование пользователя с таким же username
        if (userRepository.findByUsername(username).isPresent()) {
            List<Role> roles = roleRepository.findAll();
            model.addAttribute("roles", roles);
            model.addAttribute("error", "Username already exists. Please choose another one.");
            return "addUser";
        }

        // Хэширование пароля
        String encodedPassword = passwordEncoder.encode(password);

        // Извлечение ролей из списка идентификаторов
        Set<Role> roles = new HashSet<>();
        if (roleIds != null) {
            for (Integer roleId : roleIds) {
                roleRepository.findById(roleId).ifPresent(roles::add);
            }
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword); // Устанавливаем хэшированный пароль
        newUser.setSurname(surname);
        newUser.setName(name);
        newUser.setMiddleName(middleName);
        newUser.setSnm(snm);
        newUser.setSnmInTheGenitiveCase(snmInGenitive);
        newUser.setSnmInTheDativeCase(snmInDative);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setWorkPhoneNumber(workPhoneNumber);
        newUser.setEmail(email);
        newUser.setPositionAtWork(positionAtWork);
        newUser.setRoles(roles);

        userRepository.save(newUser);

        return "redirect:/users";
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users"; // Убедитесь, что у вас есть файл templates/users.html
    }

    @GetMapping("/edit_user/{id}")
    public String editUser(@PathVariable("id") Integer id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        return "editUser"; // Убедитесь, что у вас есть файл templates/editUser.html
    }

    @PostMapping("/delete_user")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/users"; // Редирект на страницу со списком пользователей после удаления
    }

    @PostMapping("/edit_user")
    public String saveUser(
            @RequestParam("id") Integer id,
            @RequestParam("username") String username,
            @RequestParam("surname") String surname,
            @RequestParam("name") String name,
            @RequestParam(value = "middle_name", required = false) String middleName,
            @RequestParam(value = "snm", required = false) String snm,
            @RequestParam(value = "snm_in_the_genitive_case", required = false) String snmInGenitive,
            @RequestParam(value = "snm_in_the_dative_case", required = false) String snmInDative,
            @RequestParam("phone_number") String phoneNumber,
            @RequestParam(value = "work_phone_number", required = false) String workPhoneNumber,
            @RequestParam("email") String email,
            @RequestParam(value = "position_at_work", required = false) String positionAtWork,
            @RequestParam(value = "roles", required = false) List<Integer> roleIds,
            Model model) {

        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/users";
        }

        // Обновляем поля пользователя
        user.setUsername(username);
        user.setSurname(surname);
        user.setName(name);
        user.setMiddleName(middleName);
        user.setSnm(snm);
        user.setSnmInTheGenitiveCase(snmInGenitive);
        user.setSnmInTheDativeCase(snmInDative);
        user.setPhoneNumber(phoneNumber);
        user.setWorkPhoneNumber(workPhoneNumber);
        user.setEmail(email);
        user.setPositionAtWork(positionAtWork);

        // Извлечение ролей из списка идентификаторов
        Set<Role> roles = new HashSet<>();
        if (roleIds != null) {
            for (Integer roleId : roleIds) {
                roleRepository.findById(roleId).ifPresent(roles::add);
            }
        }

        user.setRoles(roles);

        userService.save(user);

        return "redirect:/users";
    }
}