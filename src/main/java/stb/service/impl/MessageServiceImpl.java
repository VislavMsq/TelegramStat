package stb.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stb.entity.Channel;
import stb.entity.Post;
import stb.entity.User;
import stb.entity.enums.InteractionType;
import stb.repository.MessageRepository;
import stb.service.MessageService;
import stb.util.Util;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public void proceedMessage(Channel channel, Post post, Message message, User user, InteractionType interactionType) {
        stb.entity.Message messageEntity = new stb.entity.Message();

        if (Objects.requireNonNull(interactionType) == InteractionType.CLICK) {
            messageEntity.setText("BUTTON INTERACTION");
        } else if (interactionType == InteractionType.MESSAGE) {
            messageEntity.setText(message.getText());
        }

        messageEntity.setInteractionType(interactionType);
        messageEntity.setChannel(channel);
        messageEntity.setPost(post);
        messageEntity.setMessageTime(Util.convertToLocalDateTime(message.getDate()));
        messageEntity.setUser(user);
        messageRepository.save(messageEntity);
    }

    @Override
    @Transactional
    public void proceedMessage(Channel channel, Post post, Message message, User user) {
        proceedMessage(channel, post, message, user, InteractionType.MESSAGE);
    }
}