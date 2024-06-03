package stb.service;

import stb.entity.Channel;
import stb.entity.Post;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface PostService {
    Post getPost(Channel channel, Message reply);
}
