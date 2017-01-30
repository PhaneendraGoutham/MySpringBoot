package com.forsrc.boot.jms.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.*;

@Component
public class UserQueue {

    public static final String QUEUE_NAME = "q/user";

    @Autowired
    private JmsTemplate jmsTemplate;

    @Resource(name = "userQueueBean")
    private Queue userQueueBean;

    @Bean(name = "userQueueBean")
    public Queue userQueueBean() {
        return new ActiveMQQueue(QUEUE_NAME);
    }


    public void send(final UserMessage userMessage) {
        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String json = mapper.writeValueAsString(userMessage);
                    return session.createTextMessage(json);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return session.createTextMessage(e.getMessage());
                }

            }
        };
        jmsTemplate.send(userQueueBean, messageCreator);
    }


    @Component
    public class UserQueueReceiver implements MessageListener {
        @JmsListener(destination = QUEUE_NAME)
        public void onMessage(Message message) {
            System.out.println("--> UserQueue: " + message);
            try {
                if (message instanceof ActiveMQTextMessage) {
                    String json = ((ActiveMQTextMessage) message).getText();
                    if (json.startsWith("{") && json.endsWith("}")) {
                        ObjectMapper mapper = new ObjectMapper();
                        UserMessage userMessage = mapper.readValue(json, UserMessage.class);
                        System.out.println("--> UserQueue: " + userMessage.toString());
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
