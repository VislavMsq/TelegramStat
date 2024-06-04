package org.stb.service;

import org.stb.entity.Channel;
import org.stb.entity.User;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserService {
    User getUser(Message message, Channel channel);
}
