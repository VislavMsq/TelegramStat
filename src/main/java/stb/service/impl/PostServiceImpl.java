package stb.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stb.entity.Channel;
import stb.entity.Post;
import stb.repository.PostRepository;
import stb.service.PostService;
import stb.util.Util;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Override
    public Post getPost(Channel channel, Message reply) {
        Post post = new Post();
        if (!postRepository.existsByTelegramId(reply.getMessageId())) {
            post.setTelegramId(reply.getMessageId());
            post.setChannel(channel);
            post.setMessages(new HashSet<>());
            post.setPostTime(Util.convertToLocalDateTime(reply.getDate()));
            postRepository.save(post);
        } else {
            post = postRepository.findFirstByTelegramId(reply.getMessageId());
            postRepository.save(post);
        }
        return post;
    }
}
