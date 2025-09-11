package Let.s._Talk.Controller;

import Let.s._Talk.Model.Message;
import Let.s._Talk.Repository.MessageRepository;
import Let.s._Talk.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send/{senderId}/{receiverId}/{content}")
    public ResponseEntity<Message> sendMessage(@PathVariable Long senderId,
                                               @PathVariable Long receiverId,
                                               @PathVariable String content){

        Message message1 = messageService.sendMessage(senderId, receiverId, content);

        return new ResponseEntity<>(message1, HttpStatus.CREATED);
    }

    @GetMapping("/{user1}/{user2}")
    public ResponseEntity<List<Message>> getChat(@PathVariable Long user1,
                                                 @PathVariable Long user2){
        List<Message> msgs = messageService.getChat(user1, user2);

        return ResponseEntity.ok(msgs);
    }

    @PutMapping("/read/{msgId}")
    public ResponseEntity<Message> markAsRead(@PathVariable Long msgId){

        Message msg = messageService.markAsRead(msgId);

        return ResponseEntity.ok(msg);
    }

    @PutMapping("/edit/{msgId}/{senderId}")
    public ResponseEntity<Message> editMessage(@PathVariable Long msgId,
                                               @PathVariable Long senderId,
                                               @RequestBody String newContent){
        Message msg = messageService.editMessage(msgId, senderId, newContent);

        return ResponseEntity.ok(msg);
    }

    @DeleteMapping("/delete/{messageId}/{senderId}")
    public ResponseEntity<Message> deleteMessage(@PathVariable Long messageId,
                                                 @PathVariable Long senderId){
        Message msg = messageService.deleteMessage(messageId, senderId);

        return ResponseEntity.ok(msg);
    }

    @GetMapping("/unread/{receiverId}")
    public ResponseEntity<List<Message>> getUnreadMsgs(@PathVariable Long receiverId){
        List<Message> messages = messageService.getUnreadMsgs(receiverId);

        return ResponseEntity.ok(messages);
    }
}