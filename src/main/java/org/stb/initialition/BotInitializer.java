package org.stb.initialition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.example.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.stb.bot.TGBot;
import org.stb.entity.*;
import org.stb.repository.ChannelRepository;
import org.stb.repository.WebStatsHistoryRepository;
import org.stb.entity.*;
import org.stb.repository.WebStatsRepository;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.stb.bot.BotMethods.checkUser;

@Getter
@Component
@RequiredArgsConstructor
public class BotInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotInitializer.class);
    private final TransactionTemplate transactionTemplate;

    private final TGBot telegramBot;
    private final ChannelRepository channelRepository;
    private final WebStatsRepository webStatsRepository;
    private final WebStatsHistoryRepository webStatsHistoryRepository;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            LOGGER.error(e.getMessage());
            return;
        }
        try {
            userListener(telegramBot);
            try {
                Example.main(new String[]{});
            } catch (Exception e) {
                LOGGER.error("TDLib error: ", e);
            }
            tdLibUpdater();
            telegramBotsApi.registerBot(telegramBot);
            LOGGER.info("готовий до роботи");
        } catch (TelegramApiException e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void tdLibUpdater() {
        Thread thread = new Thread(() -> {
            while (true) {
                List<Channel> channels = channelRepository.findAll();
                for (Channel channel : channels) {
                    Set<Post> posts = channel.getPost();
                    System.out.println(posts);
                    for (Post post : posts) {
                        int localId = post.getTelegramId();

                        WebStats webStats = webStatsRepository.findFirstByChannelIdAndLocalId(channel.getChannelId(), localId);

                        if (webStats == null) {
                            continue;
                        }

                        TdApi.Message message = Example.getMessageById(channel.getChannelId(), webStats.getGlobalId()).join();

                        int views = message.interactionInfo.viewCount;
                        int replyCount = 0;
                        if (message.interactionInfo.replyInfo != null) {
                            replyCount = message.interactionInfo.replyInfo.replyCount;
                        }

                        TdApi.MessageReactions reaction = message.interactionInfo.reactions;
                        WebStatsHistory webStatsHistory = new WebStatsHistory();
                        Integer reactionCount = 0;

                        if (reaction == null) {
                            webStats.setReactionCount(0);
                            webStatsHistory.setReactionCount(0);
                            webStatsHistory.setLastUpdateReaction(webStats.getLastUpdateReaction());
                        } else {
                            reactionCount = Arrays.stream(reaction.reactions)
                                    .map(reaction1 -> reaction1.totalCount)
                                    .reduce(0, Integer::sum);

                            if (!Objects.equals(webStats.getReactionCount(), reactionCount)) {
                                webStats.setReactionCount(reactionCount);
                                webStats.setLastUpdateReaction(LocalDateTime.now());
                                webStatsHistory.setReactionCount(reactionCount);
                                webStatsHistory.setLastUpdateReaction(LocalDateTime.now());
                            } else {
                                webStatsHistory.setLastUpdateReaction(webStats.getLastUpdateReaction());
                                webStatsHistory.setReactionCount(webStats.getReactionCount());
                            }
                        }

                        if (!Objects.equals(webStats.getReplyCount(), replyCount)) {
                            webStats.setReplyCount(replyCount);
                            webStats.setLastUpdateReply(LocalDateTime.now());
                            webStatsHistory.setReplyCount(replyCount);
                            webStatsHistory.setLastUpdateReply(LocalDateTime.now());
                        } else {
                            webStatsHistory.setLastUpdateReply(webStats.getLastUpdateReply());
                            webStatsHistory.setReplyCount(webStats.getReplyCount());
                        }

                        if (!Objects.equals(webStats.getViewCount(), views)) {
                            webStats.setViewCount(views);
                            webStats.setLastUpdateView(LocalDateTime.now());
                            webStatsHistory.setViewCount(views);
                            webStatsHistory.setLastUpdateView(LocalDateTime.now());
                        } else {
                            webStatsHistory.setLastUpdateView(webStats.getLastUpdateView());
                            webStatsHistory.setViewCount(webStats.getViewCount());
                        }
                        webStatsHistory.setWebStats(webStats);

                        System.out.println(webStats);
                        System.out.println(webStatsHistory);

                        transactionTemplate.execute((TransactionCallback<Void>) status -> {
                            if (webStats.getLastUpdateReaction().equals(webStatsHistory.getLastUpdateReaction()) &&
                                    webStats.getLastUpdateReply().equals(webStatsHistory.getLastUpdateReply()) &&
                                    webStats.getLastUpdateView().equals(webStatsHistory.getLastUpdateView())) {
                                return null;
                            }
                            WebStats updatedWebStats = webStatsRepository.saveAndFlush(webStats); // Сохраняем обновленную сущность WebStats
                            webStatsHistory.setWebStats(updatedWebStats);
                            webStatsHistoryRepository.save(webStatsHistory);
                            return null;
                        });
                    }
                }
                try {
                    Thread.sleep((long) (1000 * 60 * 0.5));
                } catch (InterruptedException e) {
                    LOGGER.error("tdLibUpdater interrupted: ", e);
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // написати запускач потоку що буде запускатись окремим потоком та робити ці речі
    /*
    transactionTemplate.execute((TransactionCallback<Void>) status -> {
    webStatsRepository.saveAndFlush(webStats); // Используйте saveAndFlush вместо save
    webStatsHistoryRepository.saveAndFlush(webStatsHistory);
    return null;
});


       private void checkUser(User user, TGBot tgBot) throws TelegramApiException {
        if (user.getLeaveTime() != null && user.getLeaveTime().isBefore(user.getLeaveTime().minusDays(4))) {
            toMessage(user.getTelegramId(), "прийди до нас у нас є багато цікових новин", tgBot);
        } else if (user.getLastMessageTime().isBefore(user.getLastMessageTime().minusDays(2))) {
            toMessage(user.getTelegramId(), "незабувайте про наш чат", tgBot);
        }
    }
     */
    // в окремумо ваайлту циклы та буде даемоном
    private void userListener(TGBot bot) {
        Thread thread = new Thread(() -> {
            while (true) {
                // Get the list of users
                List<User> users = channelRepository.findAllWithUsers().stream() // -> stream<Channel>
//                        .filter(c -> {
//                            if (c.getTimeout() == null) {
//                                return false;
//                            }
//                            return LocalDateTime.now().isBefore(c.getTimeout());
//                        })
                        .map(Channel::getUsers) // -> stream<Set<User>>
                        .flatMap(Collection::stream) // -> stream<User>
                        .collect(Collectors.toList()); // -> list<user>

                for (User user : users) {
                    try {
                        checkUser(user, bot);
                    } catch (TelegramApiException ignore) {
                    }
                }
                // Sleep for a while before the next check
                try {
                    Thread.sleep(1000 * 60 * 60); // Sleep for 1 hour
                } catch (InterruptedException e) {
                    LOGGER.error("User listener interrupted: ", e);
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
