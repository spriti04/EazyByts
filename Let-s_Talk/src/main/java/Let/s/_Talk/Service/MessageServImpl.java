package Let.s._Talk.Service;

import Let.s._Talk.Model.Message;
import Let.s._Talk.Model.Person;
import Let.s._Talk.Repository.MessageRepository;
import Let.s._Talk.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServImpl implements MessageService{

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private PersonRepository personRepo;

    @Override
    public Message sendMessage(Long sid, Long rid, String content) {
        Person p1 = personRepo.findById(sid)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Person p2 = personRepo.findById(rid)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message(p1, p2, content);

        return messageRepo.save(message);
    }

    @Override
    public List<Message> getChat(Long user1Id, Long user2Id) {

        return messageRepo.findConversation(user1Id, user2Id);
    }

    @Override
    public Message markAsRead(Long msgId) {
        Message msg = messageRepo.findById(msgId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        msg.setRead(true);

        return messageRepo.save(msg);
    }

    @Override
    public Message editMessage(Long msgId, Long senderId, String newCont) {
        Message msg = messageRepo.findById(msgId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if(!msg.getSender().getId().equals(senderId)){
            throw new RuntimeException("You can only edit your own messages!");
        }

        msg.setContent(newCont);
        return messageRepo.save(msg);
    }

    @Override
    public Message deleteMessage(Long msgId, Long senderId) {
        Message msg = messageRepo.findById(msgId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!msg.getSender().getId().equals(senderId)){
            throw new RuntimeException("You can only delete your own messages!");
        }

        msg.setDeleted(true);
        return messageRepo.save(msg);
    }

    @Override
    public List<Message> getUnreadMsgs(Long receiverId) {
        return messageRepo.findByReceiverIdAndIsReadFalse(receiverId);
    }
}
