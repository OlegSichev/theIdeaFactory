package oleg.sichev.theideafactory.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String surname;
    private String middleName; // Отчество
    private String snm; // name + surname + middleName = snm
    private String snmInTheGenitiveCase; // Имя в родительном падеже
    private String snmInTheDativeCase; // Имя в дательном падеже
    private String phoneNumber;
    private String workPhoneNumber;
    private String departmentAtWork; // Структурное подразделение
    private String positionAtWork; // Должность

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String email;
//    @Column(nullable = false, unique = true)
//    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "the_idea_factory_entity_id"))
    private Set<TheIdeaFactoryEntity> likedIdeas = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // Геттеры и сеттеры

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSnm() {
        return snm;
    }

    public void setSnm(String snm) {
        this.snm = snm;
    }

    public String getSnmInTheGenitiveCase() {
        return snmInTheGenitiveCase;
    }

    public void setSnmInTheGenitiveCase(String snmInTheGenitiveCase) {
        this.snmInTheGenitiveCase = snmInTheGenitiveCase;
    }

    public String getSnmInTheDativeCase() {
        return snmInTheDativeCase;
    }

    public void setSnmInTheDativeCase(String snmInTheDativeCase) {
        this.snmInTheDativeCase = snmInTheDativeCase;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWorkPhoneNumber() {
        return workPhoneNumber;
    }

    public void setWorkPhoneNumber(String workPhoneNumber) {
        this.workPhoneNumber = workPhoneNumber;
    }

    public String getPositionAtWork() {
        return positionAtWork;
    }

    public String getDepartmentAtWork() {
        return departmentAtWork;
    }

    public void setDepartmentAtWork(String departmentAtWork) {
        this.departmentAtWork = departmentAtWork;
    }

    public void setPositionAtWork(String positionAtWork) {
        this.positionAtWork = positionAtWork;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<TheIdeaFactoryEntity> getLikedIdeas() {
        return likedIdeas;
    }

    public void setLikedIdeas(Set<TheIdeaFactoryEntity> likedIdeas) {
        this.likedIdeas = likedIdeas;
    }
}