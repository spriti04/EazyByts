package Let.s._Talk.Model;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Person sender;

    @ManyToOne
    private Person receiver;

    private String sname;
    private String rname;

    private String content;
    private LocalDate sentDate = LocalDate.now();
    private LocalTime sentTime = LocalTime.now();

    private boolean isRead = false;
    private boolean isDeleted = false;



    public Message() {

    }

    public Message(Person sender, Person receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.sname = sender.getName();
        this.rname = receiver.getName();
        this.content = content;
        this.sentDate = LocalDate.now();
        this.sentTime = LocalTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getSender() {
        return sender;
    }

    public void setSender(Person sender) {
        this.sender = sender;
        if(sender != null){
            this.sname = sender.getName();
        }
    }

    public Person getReceiver() {
        return receiver;
    }

    public void setReceiver(Person receiver) {
        this.receiver = receiver;
        if(receiver != null){
            this.rname = receiver.getName();
        }
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDate sentDate) {
        this.sentDate = sentDate;
    }

    public LocalTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalTime sentTime) {
        this.sentTime = sentTime;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
