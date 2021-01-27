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


}
