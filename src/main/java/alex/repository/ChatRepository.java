//package alex.repository;
//
//import alex.entity.Category;
//import alex.entity.Chat;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface ChatRepository {
//
//    @Query("select c from Chat c where c.id = :id")
//    List<Chat> findByChatId(@Param("id") long id);
//
//    @Query("select c from Chat c where c.id = :id")
//    void addNewChat(@Param("id") long id, @Param("token") String token, @Param("messenger_id") int messenger_id);
//
//
//    //реализовать запрос на выход пользователя и удаление всех его chatId по его токену
//}
