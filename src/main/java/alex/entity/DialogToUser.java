package alex.entity;

import alex.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "dialog_to_user")
public class DialogToUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //реализация связи многие ко многим (диалоги - пользователи)
    @ManyToOne
    @JoinColumn(name = "dialog_id")
    private Dialog dialog;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(mappedBy = "dialogToUser",fetch = FetchType.EAGER)
    private Collection<Favorites> favorites;


    @JsonIgnore
    public Collection<Favorites> getFavorites() {
        return favorites;
    }



    public void setFavorites(Collection<Favorites> favorites) {
        this.favorites = favorites;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
