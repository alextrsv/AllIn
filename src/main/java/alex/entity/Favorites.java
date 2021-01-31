package alex.entity;

import javax.persistence.*;

@Entity
@Table(name = "favorites")
public class Favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "dialog_to_user_id")
    private DialogToUser dialogToUser;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DialogToUser getDialogToUser() {
        return dialogToUser;
    }

    public void setDialogToUser(DialogToUser dialogToUser) {
        this.dialogToUser = dialogToUser;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
