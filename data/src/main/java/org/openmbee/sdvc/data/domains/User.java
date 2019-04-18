package org.openmbee.sdvc.data.domains;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")})
public class User extends Base {

    private String username;
    private String email;
    private String firstName;
    private String lastName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private boolean enabled;

    @OneToMany(mappedBy = "user")
    private Collection<UsersProjects> projects;

    public User() {
    }

    public User(String email, String username, String password, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String email, String username, String password, String firstName, String lastName,
        Set<UsersProjects> projects) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.projects = projects;
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

    public void encodePassword(String password) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        this.password = bcrypt.encode(password);
    }

    public void updatePassword(String old, String newPass1, String newPass2) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        if (!password.equals(bcrypt.encode(old))) {
            throw new IllegalArgumentException("Existing Password invalid");
        }
        if (!newPass1.equals(newPass2)) {
            throw new IllegalArgumentException("New Passwords don't match");
        }
        this.password = bcrypt.encode(newPass1);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Collection<UsersProjects> getProjects() {
        return projects;
    }

    public void setProjects(Collection<UsersProjects> projects) {
        this.projects = projects;
    }

    public void addProject(Project project) {
        UsersProjects usersProjects = new UsersProjects(project, this);
        projects.add(usersProjects);
        project.getUsers().add(usersProjects);
    }

    public void removeProject(Project project) {
        for (UsersProjects usersProject : projects) {
            if (usersProject.getUser().equals(this) && usersProject.getProject().equals(project)) {
                projects.remove(usersProject);
                project.removeUser(this);
            }
        }
    }

    public Collection<Role> getRoles() {
        Collection<Role> roles = new HashSet<>();
        if (!projects.isEmpty()) {
            for (UsersProjects usersProject : projects) {
                Role role = usersProject.getProject().getRoleForUser(this);
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }

    public Role getRoleForProject(Project project) {
        return project.getRoleForUser(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || id == null || getClass() != o.getClass()) {
            return false;
        }

        User node = (User) o;

        return id.equals(node.id);
    }

}
