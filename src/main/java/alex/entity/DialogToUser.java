package alex.entity;

import alex.service.UserService;

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

    @OneToMany(mappedBy = "dialogToUser")
    private Collection<Favorites> favorites;



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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
