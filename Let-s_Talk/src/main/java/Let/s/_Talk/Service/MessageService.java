package Let.s._Talk.Service;

import Let.s._Talk.Model.Message;
import Let.s._Talk.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {

    public Message sendMessage(Long sid, Long rid, String content);

    public List<Message> getChat(Long user1Id, Long user2Id);

    public Message markAsRead(Long msgId);

    public Message editMessage(Long msgId, Long senderId, String newCont);

    public Message deleteMessage(Long msgId, Long senderId);

    public List<Message> getUnreadMsgs(Long receiverId);


}
