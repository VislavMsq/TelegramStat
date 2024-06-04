package org.stb.service;

import org.stb.entity.Channel;
import org.stb.entity.Post;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface PostService {
    Post getPost(Channel channel, Message reply);
}
