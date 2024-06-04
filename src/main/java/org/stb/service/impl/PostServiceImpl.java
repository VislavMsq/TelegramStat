package org.stb.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stb.entity.Channel;
import org.stb.repository.PostRepository;
import org.stb.util.Util;
import org.stb.entity.Post;
import org.stb.service.PostService;
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
