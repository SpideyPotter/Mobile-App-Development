package com.example.electronicmail;

import java.util.Properties;
import javax.mail.*;

public class EmailFetchUtil {

    public static String fetchLatestEmail(String user, String password) throws Exception {
        Properties props = new Properties();
        props.put("mail.imap.host", "imap.gmail.com");
        props.put("mail.imap.port", "993");
        props.put("mail.imap.ssl.enable", "true");

        Session session = Session.getDefaultInstance(props);
        Store store = session.getStore("imap");
        store.connect("imap.gmail.com", user, password);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages();
        Message lastMessage = messages[messages.length - 1];
        String subject = lastMessage.getSubject();
        String content = lastMessage.getContent().toString();

        inbox.close(false);
        store.close();

        return "Subject: " + subject + "\nContent: " + content;
    }
}