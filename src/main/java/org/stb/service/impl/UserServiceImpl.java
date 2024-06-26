package org.stb.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stb.entity.Channel;
import org.stb.util.Util;
import org.stb.entity.User;
import org.stb.repository.UserRepository;
import org.stb.service.UserService;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getUser(Message message, Channel channel) {
        User user = new User();
        if (!userRepository.existsByChannelAndTelegramId(channel, message.getFrom().getId())) {
            user.setTelegramId(message.getFrom().getId());
            user.setChannel(channel);
            user.setJoinTime(null);
            user.setLastMessageTime(Util.convertToLocalDateTime(message.getDate()));
            user.setLeaveTime(null);
            userRepository.save(user);
        } else {
            user = userRepository.findFirstByTelegramId(message.getFrom().getId());
            user.setLastMessageTime(Util.convertToLocalDateTime(message.getDate()));
            userRepository.save(user);
        }
        return user;
    }

}
