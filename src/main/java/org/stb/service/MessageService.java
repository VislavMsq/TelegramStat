package org.stb.service;

import org.stb.entity.Channel;
import org.stb.entity.Post;
import org.stb.entity.User;
import org.stb.entity.enums.InteractionType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageService {
    void proceedMessage(Channel channel, Post post, Message message, User user, InteractionType interactionType);
    void proceedMessage(Channel channel, Post post, Message message, User user);
}
