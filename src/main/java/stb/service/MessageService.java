package stb.service;

import stb.entity.Channel;
import stb.entity.Post;
import stb.entity.User;
import stb.entity.enums.InteractionType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageService {
    void proceedMessage(Channel channel, Post post, Message message, User user, InteractionType interactionType);
    void proceedMessage(Channel channel, Post post, Message message, User user);
}
