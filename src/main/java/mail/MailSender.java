package mail;


import mail.model.EmailMessageDto;

public interface MailSender {
    void send(EmailMessageDto emailMessageDto);
}
