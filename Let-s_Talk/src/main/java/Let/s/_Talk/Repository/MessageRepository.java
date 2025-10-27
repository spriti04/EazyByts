package Let.s._Talk.Repository;

import Let.s._Talk.Model.Message;
import Let.s._Talk.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE ((m.sender.id = :user1 AND m.receiver.id = :user2) " +
            " OR (m.sender.id = :user2 AND m.receiver.id = :user1)) AND m.isDeleted = false ORDER BY m.sentDate ASC")
    List<Message> findConversation(@Param("user1") Long user1, @Param("user2") Long user2);

    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);
}
