package stb.service;

import stb.entity.Channel;
import stb.entity.User;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserService {
    User getUser(Message message, Channel channel);
}
