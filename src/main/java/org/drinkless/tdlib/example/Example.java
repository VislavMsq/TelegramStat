//
// Copyright Aliaksei Levin (levlam@telegram.org), Arseny Smirnov (arseny30@gmail.com) 2014-2024
//
// Distributed under the Boost Software License, Version 1.0. (See accompanying
// file LICENSE_1_0.txt or copy at http://www.boost.org/LICENSE_1_0.txt)
//
package org.drinkless.tdlib.example;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stb.bot.BotMethods;
import org.stb.util.Debug;

import java.io.IOError;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Example class for TDLib usage from Java.
 */
public final class Example {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotMethods.class);

    private static Client client = null;

    private static TdApi.AuthorizationState authorizationState = null;
    private static volatile boolean haveAuthorization = false;
    private static volatile boolean needQuit = false;
    private static volatile boolean canQuit = false;

    private static final Scanner scanner = new Scanner(System.in);

    private static final Client.ResultHandler defaultHandler = new DefaultHandler();

    private static final Lock authorizationLock = new ReentrantLock();
    private static final Condition gotAuthorization = authorizationLock.newCondition();

    private static final ConcurrentMap<Long, TdApi.User> users = new ConcurrentHashMap<Long, TdApi.User>();
    private static final ConcurrentMap<Long, TdApi.BasicGroup> basicGroups = new ConcurrentHashMap<Long, TdApi.BasicGroup>();
    private static final ConcurrentMap<Long, TdApi.Supergroup> supergroups = new ConcurrentHashMap<Long, TdApi.Supergroup>();
    private static final ConcurrentMap<Integer, TdApi.SecretChat> secretChats = new ConcurrentHashMap<Integer, TdApi.SecretChat>();

    private static final ConcurrentMap<Long, TdApi.Chat> chats = new ConcurrentHashMap<Long, TdApi.Chat>();
    private static final NavigableSet<OrderedChat> mainChatList = new TreeSet<OrderedChat>();
    private static boolean haveFullMainChatList = false;

    private static final ConcurrentMap<Long, TdApi.UserFullInfo> usersFullInfo = new ConcurrentHashMap<Long, TdApi.UserFullInfo>();
    private static final ConcurrentMap<Long, TdApi.BasicGroupFullInfo> basicGroupsFullInfo = new ConcurrentHashMap<Long, TdApi.BasicGroupFullInfo>();
    private static final ConcurrentMap<Long, TdApi.SupergroupFullInfo> supergroupsFullInfo = new ConcurrentHashMap<Long, TdApi.SupergroupFullInfo>();

    private static final String newLine = System.getProperty("line.separator");
    private static final String commandsLine = "Enter command (gcs - GetChats, gc <chatId> - GetChat, me - GetMe, sm <chatId> <message> - SendMessage, lo - LogOut, q - Quit): ";
    private static volatile String currentPrompt = null;

    private static void print(String str) {
        if (currentPrompt != null) {
            System.out.println("");
        }
        System.out.println(str);
        if (currentPrompt != null) {
            System.out.print(currentPrompt);
        }
    }

    /**
     * Retrieves the first message with the specified text in a chat.
     *
     * @param chatId The ID of the chat.
     * @param limit  The maximum number of messages to retrieve.
     * @param text   The text to search for in the messages.
     * @return A CompletableFuture that completes with the first message with the specified text, or completes exceptionally if no such message is found.
     */
    public static CompletableFuture<TdApi.Message> getMessageWithText(long chatId, int limit, String text) {
        CompletableFuture<TdApi.Message> future = new CompletableFuture<>();
        TdApi.GetChatHistory getChatHistory = new TdApi.GetChatHistory(chatId, 0, 0, limit, false);

        if (text == null) {
            future.complete(null);
            return future;
        }

        client.send(getChatHistory, object -> {
            try {
                if (object.getConstructor() == TdApi.Messages.CONSTRUCTOR) {
                    TdApi.Messages msgs = (TdApi.Messages) object;
                    for (TdApi.Message msg : msgs.messages) {
                        String content = null;
                        switch (msg.content.getConstructor()) {
                            case TdApi.MessageText.CONSTRUCTOR:
                                System.out.println("just text");
                                TdApi.MessageText contentText = (TdApi.MessageText) msg.content;
                                System.out.println(contentText);
                                content = contentText.text.text;
                                break;
                            case TdApi.MessageAnimation.CONSTRUCTOR:
                                System.out.println("animation");
//                                content = ((TdApi.MessageAnimation) msg.content).caption.text;
                                TdApi.MessageAnimation contentAnimation = (TdApi.MessageAnimation) msg.content;
                                System.out.println(contentAnimation);
                                content = contentAnimation.caption.text;
                                break;
                            case TdApi.MessageAudio.CONSTRUCTOR:
                                System.out.println("audio");
//                                content = ((TdApi.MessageAudio) msg.content).caption.text;
                                TdApi.MessageAudio contentAudio = (TdApi.MessageAudio) msg.content;
                                System.out.println(contentAudio);
                                content = contentAudio.caption.text;
                                break;
                            case TdApi.MessageDocument.CONSTRUCTOR:
                                System.out.println("document");
//                                content = ((TdApi.MessageDocument) msg.content).caption.text;
                                TdApi.MessageDocument contentDocument = (TdApi.MessageDocument) msg.content;
                                System.out.println(contentDocument);
                                content = contentDocument.caption.text;
                                break;
                            case TdApi.MessagePhoto.CONSTRUCTOR:
                                System.out.println("photo");
//                                content = ((TdApi.MessagePhoto) msg.content).caption.text;
                                TdApi.MessagePhoto contentPhoto = (TdApi.MessagePhoto) msg.content;
                                System.out.println(contentPhoto);
                                content = contentPhoto.caption.text;
                                break;
                            case TdApi.MessageVideo.CONSTRUCTOR:
                                System.out.println("video");
//                                content = ((TdApi.MessageVideo) msg.content).caption.text;
                                TdApi.MessageVideo contentVideo = (TdApi.MessageVideo) msg.content;
                                System.out.println(contentVideo);
                                content = contentVideo.caption.text;
                                break;
                            default:
                                System.out.println("default");
                                System.out.println(msg.content.getConstructor());
                                System.out.println(msg.content);
                                break;
                        }
                        if (content == null) {
                            LOGGER.warn("контент (текст) пустий");
                            continue;
                        }
                        LOGGER.info("\ntext1:{}\ntext2:{}\ncontains:{}\nequals{}", content, text, content.contains(text), content.equals(text));
                        if (content.contains(text)) {
                            future.complete(msg);
                            return;
                        }
                    }
                    LOGGER.error("Message not found");
//                    LOGGER.error("Received response from TDLib: " + object);
//                    LOGGER.info("Content: " + msgs);
//                    LOGGER.info("Text: " + text);
                    future.complete(null);
                } else if (object.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                    LOGGER.error("Received an error: " + object);
                    future.complete(null);
                } else {
                    LOGGER.error("Received wrong response from TDLib: " + object);
                    future.complete(null);
                }
            } catch (Exception e) {
                LOGGER.error("Exception occurred: " + e);
                future.complete(null);
            }
        });

        return future;
    }

    /*
    0000000000000000000000000000000
      TdApi.MessagePhoto photoContent = (TdApi.MessagePhoto) msg.content;
                        if (photoContent.caption.text.contains(text)) {
                            LOGGER.info("88");
                            future.complete(msg);
    000000000000000000000000000000000000000000000000000000000000000

    public static CompletableFuture<TdApi.Message> getMessageWithText(long chatId, int limit, String text) {
    CompletableFuture<TdApi.Message> future = new CompletableFuture<>();
    TdApi.GetChatHistory getChatHistory = new TdApi.GetChatHistory(chatId, 0, 0, limit, false);

    client.send(getChatHistory, object -> {
        if (object.getConstructor() == TdApi.Messages.CONSTRUCTOR) {
            TdApi.Messages msgs = (TdApi.Messages) object;
            for (TdApi.Message msg : msgs.messages) {
                if (msg.content.getConstructor() == TdApi.MessageText.CONSTRUCTOR) {
                    TdApi.MessageText content = (TdApi.MessageText) msg.content;
                    if (content.text.text.contains(text)) {
                        System.out.println(msg);
                        future.complete(msg);
                        return;
                    }
                }
            }
            future.completeExceptionally(new Exception("Message not found"));
        } else if (object.getConstructor() == TdApi.Error.CONSTRUCTOR) {
            future.completeExceptionally(new Exception("Receive an error: " + object));
        } else {
            future.completeExceptionally(new Exception("Receive wrong response from TDLib: " + object));
        }
    });

    return future;
}
==================================================================================
public static CompletableFuture<TdApi.Message> getMessageWithText(long chatId, int limit, String text) {
    CompletableFuture<TdApi.Message> future = new CompletableFuture<>();
    TdApi.GetChatHistory getChatHistory = new TdApi.GetChatHistory(chatId, 0, 0, limit, false);

    client.send(getChatHistory, object -> {
        if (object.getConstructor() == TdApi.Messages.CONSTRUCTOR) {
            TdApi.Messages msgs = (TdApi.Messages) object;
            for (TdApi.Message msg : msgs.messages) {
                if (msg.content.getConstructor() == TdApi.MessageText.CONSTRUCTOR) {
                    TdApi.MessageText content = (TdApi.MessageText) msg.content;
                    if (content.text.text.contains(text)) {
                        System.out.println(msg);
                        getMessageByIdAndText(chatId, msg.id, text).thenAccept(future::complete)
                            .exceptionally(ex -> {
                                future.completeExceptionally(ex);
                                return null;
                            });
                        return;
                    }
                }
            }
            future.completeExceptionally(new Exception("Message not found"));
        } else if (object.getConstructor() == TdApi.Error.CONSTRUCTOR) {
            future.completeExceptionally(new Exception("Receive an error: " + object));
        } else {
            future.completeExceptionally(new Exception("Receive wrong response from TDLib: " + object));
        }
    });

    return future;
}

     */

//    public static void getMessageWithText(long chatId, int limit, String text) {
//        TdApi.GetChatHistory getChatHistory = new TdApi.GetChatHistory(chatId, 0, 0, limit, false);
//        client.send(getChatHistory, new Client.ResultHandler() {
//            @Override
//            public void onResult(TdApi.Object object) {
//                TdApi.Messages msgs = (TdApi.Messages) object;
//
//                for (TdApi.Message msg : msgs.messages) {
//                    getMessageByIdAndText(chatId, msg.id, text);
//                }
//            }
//        });
//    }

    public static CompletableFuture<TdApi.Message> getMessageByIdAndText(long chatId, long messageId, String text) {
        CompletableFuture<TdApi.Message> future = new CompletableFuture<>();
        TdApi.GetMessage getMessageRequest = new TdApi.GetMessage(chatId, messageId);
//        TdApi.GetChat getChat = new TdApi.GetChat(chatId);


//        client.send(getChat, object -> {
//            switch (object.getConstructor()) {
//                case TdApi.GetChat.CONSTRUCTOR:
//                    TdApi.Chat chat = (TdApi.Chat) object;
//                    LOGGER.info(chat.toString());
//                case TdApi.Error.CONSTRUCTOR:
//                    LOGGER.info("Received an error: {}", object);
//                default:
//                    LOGGER.info("Received wrong response from TDLib: {}", object);
//            }
//        });


        client.send(getMessageRequest, object -> {
            switch (object.getConstructor()) {
                case TdApi.Message.CONSTRUCTOR:
                    TdApi.Message message = (TdApi.Message) object;
                    if (message.content instanceof TdApi.MessageText) {
                        TdApi.MessageText content = (TdApi.MessageText) message.content;
                        if (content.text.text.contains(text)) {
                            future.complete(message);
                        } else {
                            future.completeExceptionally(new Exception("Message does not contain the specified text"));
                        }
                    } else {
                        future.completeExceptionally(new Exception("Message content is not text"));
                    }
                    break;
                case TdApi.Error.CONSTRUCTOR:
                    future.completeExceptionally(new Exception("Received an error: " + object));
                    break;
                default:
                    future.completeExceptionally(new Exception("Received wrong response from TDLib: " + object));
            }
        });
        return future;
    }

    public static CompletableFuture<List<TdApi.Message>> getChatHistory(long chatId, int limit) {
        CompletableFuture<List<TdApi.Message>> future = new CompletableFuture<>();
        TdApi.GetChatHistory getChatHistory = new TdApi.GetChatHistory(chatId, 0, 0, limit, false);

        client.send(getChatHistory, object -> {
//            LOGGER.info("chat history");
            if (object.getConstructor() == TdApi.Messages.CONSTRUCTOR) {
                TdApi.Messages msgs = (TdApi.Messages) object;
                List<TdApi.Message> messages = Arrays.asList(msgs.messages);
//                LOGGER.info(messages.toString());
//                LOGGER.info(String.valueOf(messages.size()));
                future.complete(messages);
            } else if (object.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                future.completeExceptionally(new Exception("Receive an error: " + object));
            } else {
                future.completeExceptionally(new Exception("Receive wrong response from TDLib: " + object));
            }
        });

        return future;
    }


    public static CompletableFuture<TdApi.Message> getMessageById(long chatId, long messageId) {
        CompletableFuture<TdApi.Message> future = new CompletableFuture<>();
        TdApi.GetMessage getMessageRequest = new TdApi.GetMessage(chatId, messageId);
        client.send(getMessageRequest, object -> {
            switch (object.getConstructor()) {
                case TdApi.Message.CONSTRUCTOR:
                    TdApi.Message message = (TdApi.Message) object;
                    future.complete(message);
                    break;
                case TdApi.Error.CONSTRUCTOR:
                    future.completeExceptionally(new Exception("Receive an error: " + object));
                    break;
                default:
                    future.completeExceptionally(new Exception("Receive wrong response from TDLib: " + object));
            }
        });
        return future;
    }

//    public static void getChatMembers(long supergroupId) {
//        TdApi.GetSupergroupMembers getSupergroupMembers = new TdApi.GetSupergroupMembers(supergroupId, new TdApi.SupergroupMembersFilter() {});
//    }

    public static CompletableFuture<TdApi.ChatMembers> getSupergroupMembers(long supergroupId) {
        CompletableFuture<TdApi.ChatMembers> future = new CompletableFuture<>();
        TdApi.GetSupergroupMembers getSupergroupMembers = new TdApi.GetSupergroupMembers(supergroupId, null, 0, 200);

        client.send(getSupergroupMembers, object -> {
            if (object instanceof TdApi.ChatMembers) {
                future.complete((TdApi.ChatMembers) object);
            } else if (object instanceof TdApi.Error) {
                future.completeExceptionally(new Exception("Receive an error: " + object));
            } else {
                future.completeExceptionally(new Exception("Receive wrong response from TDLib: " + object));
            }
        });

        return future;
    }

    ////
    public static CompletableFuture<TdApi.ChatStatistics> getChatStatistics(long chatId, boolean isDark) {
        CompletableFuture<TdApi.ChatStatistics> future = new CompletableFuture<>();
        TdApi.GetChatStatistics getChatStatistics = new TdApi.GetChatStatistics(chatId, isDark);

        client.send(getChatStatistics, object -> {
            if (object instanceof TdApi.ChatStatistics) {
                future.complete((TdApi.ChatStatistics) object);
            } else if (object instanceof TdApi.Error) {
                future.completeExceptionally(new Exception("Receive an error: " + object));
            } else {
                future.completeExceptionally(new Exception("Receive wrong response from TDLib: " + object));
            }
        });

        return future;
    }

    public static CompletableFuture<TdApi.Chat> getChat(long chatId) {
        CompletableFuture<TdApi.Chat> future = new CompletableFuture<>();
        TdApi.GetChat getChat = new TdApi.GetChat(chatId);

        client.send(getChat, object -> {
            if (object instanceof TdApi.Chat) {
                future.complete((TdApi.Chat) object);
            } else if (object instanceof TdApi.Error) {
                future.completeExceptionally(new Exception("Receive an error: " + object));
            } else {
                future.completeExceptionally(new Exception("Receive wrong response from TDLib: " + object));
            }
        });

        return future;
    }

    public static void getMessageViews(long chatId, long messageId) {
        TdApi.GetMessage getMessage = new TdApi.GetMessage(chatId, messageId);
        client.send(getMessage, new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                if (object.getConstructor() == TdApi.Message.CONSTRUCTOR) {
                    TdApi.Message message = (TdApi.Message) object;
                    System.out.println("Message views: " + message.interactionInfo.viewCount);
                } else {
                    System.err.println("Failed to get message: " + object);
                }
            }
        });
    }

    private static class UpdatesHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.UpdateMessageReactions.CONSTRUCTOR:
                    TdApi.UpdateMessageReactions updateMessageReactions = (TdApi.UpdateMessageReactions) object;
                    System.out.println("Message " + updateMessageReactions.reactions);
                    break;
//                case TdApi.UpdateNewMessage.CONSTRUCTOR:
//                    TdApi.UpdateNewMessage updateNewMessage = (TdApi.UpdateNewMessage) object;
//                    System.out.println("New message from user " + updateNewMessage.message.senderUserId + ": " + updateNewMessage.message.content);
//                    break;
//                case TdApi.UpdateMessageContent.CONSTRUCTOR:
//                    TdApi.UpdateMessageContent updateMessageContent = (TdApi.UpdateMessageContent) object;
//                    System.out.println("Message content updated: " + updateMessageContent.messageId);
//                    break;
//                case TdApi.UpdateMessageViews.CONSTRUCTOR:
//                    TdApi.UpdateMessageViews updateMessageViews = (TdApi.UpdateMessageViews) object;
//                    System.out.println("Message " + updateMessageViews.messageId + " views: " + updateMessageViews.views);
//                    break;
//                // Обработка других типов обновлений
//                default:
//                    break;
            }
        }
    }


//        client.send(getChatHistory, new Client.ResultHandler() {
//            @Override
//            public void onResult(TdApi.Object object) {
//                switch (object.getConstructor()) {
//                    case TdApi.Error.CONSTRUCTOR:
//                        } else {
//                            System.err.println("Receive an error for LoadChats:" + newLine + object);
//                        }
//                        break;
//                    case TdApi.Ok.CONSTRUCTOR:
//                        // chats had already been received through updates, let's retry request
//
//                        break;
//                    default:
//                        System.err.println("Receive wrong response from TDLib:" + newLine + object);
//                }
//            }
//        });

//        for (TdApi.Message message : messages.messages) {
//            System.out.println(message.content);
//        }


    private static void setChatPositions(TdApi.Chat chat, TdApi.ChatPosition[] positions) {
        synchronized (Example.mainChatList) {
            synchronized (chat) {
                for (TdApi.ChatPosition position : chat.positions) {
                    if (position.list.getConstructor() == TdApi.ChatListMain.CONSTRUCTOR) {
                        boolean isRemoved = Example.mainChatList.remove(new OrderedChat(chat.id, position));
                        assert isRemoved;
                    }
                }

                chat.positions = positions;

                for (TdApi.ChatPosition position : chat.positions) {
                    if (position.list.getConstructor() == TdApi.ChatListMain.CONSTRUCTOR) {
                        boolean isAdded = mainChatList.add(new OrderedChat(chat.id, position));
                        assert isAdded;
                    }
                }
            }
        }
    }

    private static void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
        if (authorizationState != null) {
            Example.authorizationState = authorizationState;
        }
        switch (Example.authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                TdApi.SetTdlibParameters request = new TdApi.SetTdlibParameters();
                request.useMessageDatabase = true;
                request.useSecretChats = true;
                request.apiId = 28118758;
                request.apiHash = "98b2928c89ba5915476e11185a5dd12e";
                request.systemLanguageCode = "en";
                request.deviceModel = "Desktop";
                request.applicationVersion = "1.0";

                client.send(request, new AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
                String phoneNumber = promptString("Please enter phone number: ");
                client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null), new AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR: {
                String link = ((TdApi.AuthorizationStateWaitOtherDeviceConfirmation) Example.authorizationState).link;
                System.out.println("Please confirm this login link on another device: " + link);
                break;
            }
            case TdApi.AuthorizationStateWaitEmailAddress.CONSTRUCTOR: {
                String emailAddress = promptString("Please enter email address: ");
                client.send(new TdApi.SetAuthenticationEmailAddress(emailAddress), new AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitEmailCode.CONSTRUCTOR: {
                String code = promptString("Please enter email authentication code: ");
                client.send(new TdApi.CheckAuthenticationEmailCode(new TdApi.EmailAddressAuthenticationCode(code)), new AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR: {
                String code = promptString("Please enter authentication code: ");
                client.send(new TdApi.CheckAuthenticationCode(code), new AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR: {
                String firstName = promptString("Please enter your first name: ");
                String lastName = promptString("Please enter your last name: ");
                client.send(new TdApi.RegisterUser(firstName, lastName, false), new AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR: {
                String password = promptString("Please enter password: ");
                client.send(new TdApi.CheckAuthenticationPassword(password), new AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                haveAuthorization = true;
                authorizationLock.lock();
                try {
                    gotAuthorization.signal();
                } finally {
                    authorizationLock.unlock();
                }
                break;
            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                haveAuthorization = false;
                print("Logging out");
                break;
            case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
                haveAuthorization = false;
                print("Closing");
                break;
            case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
                print("Closed");
                if (!needQuit) {
                    client = Client.create(new UpdateHandler(), null, null); // recreate client after previous has closed
                } else {
                    canQuit = true;
                }
                break;
            default:
                System.err.println("Unsupported authorization state:" + newLine + Example.authorizationState);
        }
    }

    private void getMessageText(Long chatId, Long messageId) {
        TdApi.GetMessage getMessageRequest = new TdApi.GetMessage(chatId, messageId);
        client.send(getMessageRequest, object -> {
            switch (object.getConstructor()) {
                case TdApi.Message.CONSTRUCTOR:
                    TdApi.Message message = (TdApi.Message) object;
                    for (TdApi.MessageReaction reaction : message.interactionInfo.reactions.reactions) {
                        System.out.printf("%s -> %s%n", reaction.type, reaction.totalCount);
                        System.out.println(Arrays.toString(reaction.recentSenderIds));
                    }
                    break;
                case TdApi.Error.CONSTRUCTOR:
                    System.err.println("Receive an error:");
                    System.err.println(object);
                    break;
                default:
                    System.err.println("Receive wrong response from TDLib:");
                    System.err.println(object);
            }
        });
    }

    private static int toInt(String arg) {
        int result = 0;
        try {
            result = Integer.parseInt(arg);
        } catch (NumberFormatException ignored) {
        }
        return result;
    }

    private static long getChatId(String arg) {
        long chatId = 0;
        try {
            chatId = Long.parseLong(arg);
        } catch (NumberFormatException ignored) {
        }
        return chatId;
    }

    private static String promptString(String prompt) {
        System.out.print(prompt);
        currentPrompt = prompt;
        String str = "";
        try {
            str = scanner.nextLine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        currentPrompt = null;
        return str;
    }

    private static void getCommand() {
        String command = promptString(commandsLine);
        String[] commands = command.split(" ", 2);
        try {
            switch (commands[0]) {
                case "gcs": {
                    int limit = 20;
                    if (commands.length > 1) {
                        limit = toInt(commands[1]);
                    }
                    getMainChatList(limit);
                    break;
                }
                case "gc":
                    client.send(new TdApi.GetChat(getChatId(commands[1])), defaultHandler);
                    break;
                case "me":
                    client.send(new TdApi.GetMe(), defaultHandler);
                    break;
                case "sm": {
                    String[] args = commands[1].split(" ", 2);
                    sendMessage(getChatId(args[0]), args[1]);
                    break;
                }
                case "lo":
                    haveAuthorization = false;
                    client.send(new TdApi.LogOut(), defaultHandler);
                    break;
                case "q":
                    needQuit = true;
                    haveAuthorization = false;
                    client.send(new TdApi.Close(), defaultHandler);
                    break;
                case "gch":
                    getChatHistory(Long.parseLong(commands[1]), 50);
                default:
                    System.err.println("Unsupported command: " + command);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            print("Not enough arguments");
        }
    }

    private static void getMainChatList(final int limit) {
        synchronized (mainChatList) {
            if (!haveFullMainChatList && limit > mainChatList.size()) {
                // send LoadChats request if there are some unknown chats and have not enough known chats
                client.send(new TdApi.LoadChats(new TdApi.ChatListMain(), limit - mainChatList.size()), new Client.ResultHandler() {
                    @Override
                    public void onResult(TdApi.Object object) {
                        switch (object.getConstructor()) {
                            case TdApi.Error.CONSTRUCTOR:
                                if (((TdApi.Error) object).code == 404) {
                                    synchronized (mainChatList) {
                                        haveFullMainChatList = true;
                                    }
                                } else {
                                    System.err.println("Receive an error for LoadChats:" + newLine + object);
                                }
                                break;
                            case TdApi.Ok.CONSTRUCTOR:
                                // chats had already been received through updates, let's retry request
                                getMainChatList(limit);
                                break;
                            default:
                                System.err.println("Receive wrong response from TDLib:" + newLine + object);
                        }
                    }
                });
                return;
            }

            Iterator<OrderedChat> iter = mainChatList.iterator();
            System.out.println();
            System.out.println("First " + limit + " chat(s) out of " + mainChatList.size() + " known chat(s):");
            for (int i = 0; i < limit && i < mainChatList.size(); i++) {
                long chatId = iter.next().chatId;
                TdApi.Chat chat = chats.get(chatId);
                synchronized (chat) {
                    System.out.println(chatId + ": " + chat.title);
                }
            }
            print("");
        }
    }

    private static void sendMessage(long chatId, String message) {
        // initialize reply markup just for testing
        TdApi.InlineKeyboardButton[] row = {new TdApi.InlineKeyboardButton("https://telegram.org?1", new TdApi.InlineKeyboardButtonTypeUrl()), new TdApi.InlineKeyboardButton("https://telegram.org?2", new TdApi.InlineKeyboardButtonTypeUrl()), new TdApi.InlineKeyboardButton("https://telegram.org?3", new TdApi.InlineKeyboardButtonTypeUrl())};
        TdApi.ReplyMarkup replyMarkup = new TdApi.ReplyMarkupInlineKeyboard(new TdApi.InlineKeyboardButton[][]{row, row, row});

        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(message, null), null, true);
        client.send(new TdApi.SendMessage(chatId, 0, null, null, replyMarkup, content), defaultHandler);
    }

    public static void main(String[] args) throws InterruptedException {
        // set log message handler to handle only fatal errors (0) and plain log messages (-1)
        Client.setLogMessageHandler(0, new LogMessageHandler());

        // disable TDLib log and redirect fatal errors and plain log messages to a file
        try {
            Client.execute(new TdApi.SetLogVerbosityLevel(0));
            Client.execute(new TdApi.SetLogStream(new TdApi.LogStreamFile("tdlib.log", 1 << 27, false)));
        } catch (Client.ExecutionException error) {
            throw new IOError(new IOException("Write access to the current directory is required"));
        }

        // create client
        client = Client.create(new UpdateHandler(), null, null);

//        if (!haveAuthorization) {
//            gotAuthorization.await();
//        }

        // main loop
//        while (!needQuit) {
//            // await authorization
        authorizationLock.lock();
        try {
//                while (!haveAuthorization) {
            gotAuthorization.await();
//                }
        } finally {
            authorizationLock.unlock();
        }
//
//            while (haveAuthorization) {
//                getCommand();
//            }
//        }
//        while (!canQuit) {
//            Thread.sleep(1);
//        }
    }

    private static class OrderedChat implements Comparable<OrderedChat> {
        final long chatId;
        final TdApi.ChatPosition position;

        OrderedChat(long chatId, TdApi.ChatPosition position) {
            this.chatId = chatId;
            this.position = position;
        }

        @Override
        public int compareTo(OrderedChat o) {
            if (this.position.order != o.position.order) {
                return o.position.order < this.position.order ? -1 : 1;
            }
            if (this.chatId != o.chatId) {
                return o.chatId < this.chatId ? -1 : 1;
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            OrderedChat o = (OrderedChat) obj;
            return this.chatId == o.chatId && this.position.order == o.position.order;
        }
    }

    private static class DefaultHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            print(object.toString());
        }
    }

    private static class UpdateHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                    onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
                    break;

                case TdApi.UpdateUser.CONSTRUCTOR:
                    TdApi.UpdateUser updateUser = (TdApi.UpdateUser) object;
                    users.put(updateUser.user.id, updateUser.user);
                    break;
                case TdApi.UpdateUserStatus.CONSTRUCTOR: {
                    TdApi.UpdateUserStatus updateUserStatus = (TdApi.UpdateUserStatus) object;
                    TdApi.User user = users.get(updateUserStatus.userId);
                    synchronized (user) {
                        user.status = updateUserStatus.status;
                    }
                    break;
                }
                case TdApi.UpdateBasicGroup.CONSTRUCTOR:
                    TdApi.UpdateBasicGroup updateBasicGroup = (TdApi.UpdateBasicGroup) object;
                    basicGroups.put(updateBasicGroup.basicGroup.id, updateBasicGroup.basicGroup);
                    break;
                case TdApi.UpdateSupergroup.CONSTRUCTOR:
                    TdApi.UpdateSupergroup updateSupergroup = (TdApi.UpdateSupergroup) object;
                    supergroups.put(updateSupergroup.supergroup.id, updateSupergroup.supergroup);
                    break;
                case TdApi.UpdateSecretChat.CONSTRUCTOR:
                    TdApi.UpdateSecretChat updateSecretChat = (TdApi.UpdateSecretChat) object;
                    secretChats.put(updateSecretChat.secretChat.id, updateSecretChat.secretChat);
                    break;

                case TdApi.UpdateNewChat.CONSTRUCTOR: {
                    TdApi.UpdateNewChat updateNewChat = (TdApi.UpdateNewChat) object;
                    TdApi.Chat chat = updateNewChat.chat;
                    synchronized (chat) {
                        chats.put(chat.id, chat);

                        TdApi.ChatPosition[] positions = chat.positions;
                        chat.positions = new TdApi.ChatPosition[0];
                        setChatPositions(chat, positions);
                    }
                    break;
                }
                case TdApi.UpdateChatTitle.CONSTRUCTOR: {
                    TdApi.UpdateChatTitle updateChat = (TdApi.UpdateChatTitle) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.title = updateChat.title;
                    }
                    break;
                }
                case TdApi.UpdateChatPhoto.CONSTRUCTOR: {
                    TdApi.UpdateChatPhoto updateChat = (TdApi.UpdateChatPhoto) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.photo = updateChat.photo;
                    }
                    break;
                }
                case TdApi.UpdateChatPermissions.CONSTRUCTOR: {
                    TdApi.UpdateChatPermissions update = (TdApi.UpdateChatPermissions) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.permissions = update.permissions;
                    }
                    break;
                }
                case TdApi.UpdateChatLastMessage.CONSTRUCTOR: {
                    TdApi.UpdateChatLastMessage updateChat = (TdApi.UpdateChatLastMessage) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.lastMessage = updateChat.lastMessage;
                        setChatPositions(chat, updateChat.positions);
                    }
                    break;
                }
                case TdApi.UpdateChatPosition.CONSTRUCTOR: {
                    TdApi.UpdateChatPosition updateChat = (TdApi.UpdateChatPosition) object;
                    if (updateChat.position.list.getConstructor() != TdApi.ChatListMain.CONSTRUCTOR) {
                        break;
                    }

                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        int i;
                        for (i = 0; i < chat.positions.length; i++) {
                            if (chat.positions[i].list.getConstructor() == TdApi.ChatListMain.CONSTRUCTOR) {
                                break;
                            }
                        }
                        TdApi.ChatPosition[] new_positions = new TdApi.ChatPosition[chat.positions.length + (updateChat.position.order == 0 ? 0 : 1) - (i < chat.positions.length ? 1 : 0)];
                        int pos = 0;
                        if (updateChat.position.order != 0) {
                            new_positions[pos++] = updateChat.position;
                        }
                        for (int j = 0; j < chat.positions.length; j++) {
                            if (j != i) {
                                new_positions[pos++] = chat.positions[j];
                            }
                        }
                        assert pos == new_positions.length;

                        setChatPositions(chat, new_positions);
                    }
                    break;
                }
                case TdApi.UpdateChatReadInbox.CONSTRUCTOR: {
                    TdApi.UpdateChatReadInbox updateChat = (TdApi.UpdateChatReadInbox) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.lastReadInboxMessageId = updateChat.lastReadInboxMessageId;
                        chat.unreadCount = updateChat.unreadCount;
                    }
                    break;
                }
                case TdApi.UpdateChatReadOutbox.CONSTRUCTOR: {
                    TdApi.UpdateChatReadOutbox updateChat = (TdApi.UpdateChatReadOutbox) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.lastReadOutboxMessageId = updateChat.lastReadOutboxMessageId;
                    }
                    break;
                }
                case TdApi.UpdateChatActionBar.CONSTRUCTOR: {
                    TdApi.UpdateChatActionBar updateChat = (TdApi.UpdateChatActionBar) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.actionBar = updateChat.actionBar;
                    }
                    break;
                }
                case TdApi.UpdateChatAvailableReactions.CONSTRUCTOR: {
                    TdApi.UpdateChatAvailableReactions updateChat = (TdApi.UpdateChatAvailableReactions) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.availableReactions = updateChat.availableReactions;
                    }
                    break;
                }
                case TdApi.UpdateChatDraftMessage.CONSTRUCTOR: {
                    TdApi.UpdateChatDraftMessage updateChat = (TdApi.UpdateChatDraftMessage) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.draftMessage = updateChat.draftMessage;
                        setChatPositions(chat, updateChat.positions);
                    }
                    break;
                }
                case TdApi.UpdateChatMessageSender.CONSTRUCTOR: {
                    TdApi.UpdateChatMessageSender updateChat = (TdApi.UpdateChatMessageSender) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.messageSenderId = updateChat.messageSenderId;
                    }
                    break;
                }
                case TdApi.UpdateChatMessageAutoDeleteTime.CONSTRUCTOR: {
                    TdApi.UpdateChatMessageAutoDeleteTime updateChat = (TdApi.UpdateChatMessageAutoDeleteTime) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.messageAutoDeleteTime = updateChat.messageAutoDeleteTime;
                    }
                    break;
                }
                case TdApi.UpdateChatNotificationSettings.CONSTRUCTOR: {
                    TdApi.UpdateChatNotificationSettings update = (TdApi.UpdateChatNotificationSettings) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.notificationSettings = update.notificationSettings;
                    }
                    break;
                }
                case TdApi.UpdateChatPendingJoinRequests.CONSTRUCTOR: {
                    TdApi.UpdateChatPendingJoinRequests update = (TdApi.UpdateChatPendingJoinRequests) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.pendingJoinRequests = update.pendingJoinRequests;
                    }
                    break;
                }
                case TdApi.UpdateChatReplyMarkup.CONSTRUCTOR: {
                    TdApi.UpdateChatReplyMarkup updateChat = (TdApi.UpdateChatReplyMarkup) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.replyMarkupMessageId = updateChat.replyMarkupMessageId;
                    }
                    break;
                }
                case TdApi.UpdateChatBackground.CONSTRUCTOR: {
                    TdApi.UpdateChatBackground updateChat = (TdApi.UpdateChatBackground) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.background = updateChat.background;
                    }
                    break;
                }
                case TdApi.UpdateChatTheme.CONSTRUCTOR: {
                    TdApi.UpdateChatTheme updateChat = (TdApi.UpdateChatTheme) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.themeName = updateChat.themeName;
                    }
                    break;
                }
                case TdApi.UpdateChatUnreadMentionCount.CONSTRUCTOR: {
                    TdApi.UpdateChatUnreadMentionCount updateChat = (TdApi.UpdateChatUnreadMentionCount) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.unreadMentionCount = updateChat.unreadMentionCount;
                    }
                    break;
                }
                case TdApi.UpdateChatUnreadReactionCount.CONSTRUCTOR: {
                    TdApi.UpdateChatUnreadReactionCount updateChat = (TdApi.UpdateChatUnreadReactionCount) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.unreadReactionCount = updateChat.unreadReactionCount;
                    }
                    break;
                }
                case TdApi.UpdateChatVideoChat.CONSTRUCTOR: {
                    TdApi.UpdateChatVideoChat updateChat = (TdApi.UpdateChatVideoChat) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.videoChat = updateChat.videoChat;
                    }
                    break;
                }
                case TdApi.UpdateChatDefaultDisableNotification.CONSTRUCTOR: {
                    TdApi.UpdateChatDefaultDisableNotification update = (TdApi.UpdateChatDefaultDisableNotification) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.defaultDisableNotification = update.defaultDisableNotification;
                    }
                    break;
                }
                case TdApi.UpdateChatHasProtectedContent.CONSTRUCTOR: {
                    TdApi.UpdateChatHasProtectedContent updateChat = (TdApi.UpdateChatHasProtectedContent) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.hasProtectedContent = updateChat.hasProtectedContent;
                    }
                    break;
                }
                case TdApi.UpdateChatIsTranslatable.CONSTRUCTOR: {
                    TdApi.UpdateChatIsTranslatable update = (TdApi.UpdateChatIsTranslatable) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.isTranslatable = update.isTranslatable;
                    }
                    break;
                }
                case TdApi.UpdateChatIsMarkedAsUnread.CONSTRUCTOR: {
                    TdApi.UpdateChatIsMarkedAsUnread update = (TdApi.UpdateChatIsMarkedAsUnread) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.isMarkedAsUnread = update.isMarkedAsUnread;
                    }
                    break;
                }
                case TdApi.UpdateChatBlockList.CONSTRUCTOR: {
                    TdApi.UpdateChatBlockList update = (TdApi.UpdateChatBlockList) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.blockList = update.blockList;
                    }
                    break;
                }
                case TdApi.UpdateChatHasScheduledMessages.CONSTRUCTOR: {
                    TdApi.UpdateChatHasScheduledMessages update = (TdApi.UpdateChatHasScheduledMessages) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.hasScheduledMessages = update.hasScheduledMessages;
                    }
                    break;
                }

                case TdApi.UpdateMessageMentionRead.CONSTRUCTOR: {
                    TdApi.UpdateMessageMentionRead updateChat = (TdApi.UpdateMessageMentionRead) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.unreadMentionCount = updateChat.unreadMentionCount;
                    }
                    break;
                }
                case TdApi.UpdateMessageUnreadReactions.CONSTRUCTOR: {
                    TdApi.UpdateMessageUnreadReactions updateChat = (TdApi.UpdateMessageUnreadReactions) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.unreadReactionCount = updateChat.unreadReactionCount;
                    }
                    break;
                }

                case TdApi.UpdateUserFullInfo.CONSTRUCTOR:
                    TdApi.UpdateUserFullInfo updateUserFullInfo = (TdApi.UpdateUserFullInfo) object;
                    usersFullInfo.put(updateUserFullInfo.userId, updateUserFullInfo.userFullInfo);
                    break;
                case TdApi.UpdateBasicGroupFullInfo.CONSTRUCTOR:
                    TdApi.UpdateBasicGroupFullInfo updateBasicGroupFullInfo = (TdApi.UpdateBasicGroupFullInfo) object;
                    basicGroupsFullInfo.put(updateBasicGroupFullInfo.basicGroupId, updateBasicGroupFullInfo.basicGroupFullInfo);
                    break;
                case TdApi.UpdateSupergroupFullInfo.CONSTRUCTOR:
                    TdApi.UpdateSupergroupFullInfo updateSupergroupFullInfo = (TdApi.UpdateSupergroupFullInfo) object;
                    supergroupsFullInfo.put(updateSupergroupFullInfo.supergroupId, updateSupergroupFullInfo.supergroupFullInfo);
                    break;
                default:
                    // print("Unsupported update:" + newLine + object);
            }
        }
    }

    private static class AuthorizationRequestHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.Error.CONSTRUCTOR:
                    System.err.println("Receive an error:" + newLine + object);
                    onAuthorizationStateUpdated(null); // repeat last action
                    break;
                case TdApi.Ok.CONSTRUCTOR:
                    // result is already received through UpdateAuthorizationState, nothing to do
                    break;
                default:
                    System.err.println("Receive wrong response from TDLib:" + newLine + object);
            }
        }
    }

    private static class LogMessageHandler implements Client.LogMessageHandler {
        @Override
        public void onLogMessage(int verbosityLevel, String message) {
            if (verbosityLevel == 0) {
                onFatalError(message);
                return;
            }
            System.err.println(message);
        }
    }

    private static void onFatalError(String errorMessage) {
        final class ThrowError implements Runnable {
            private final String errorMessage;
            private final AtomicLong errorThrowTime;

            private ThrowError(String errorMessage, AtomicLong errorThrowTime) {
                this.errorMessage = errorMessage;
                this.errorThrowTime = errorThrowTime;
            }

            @Override
            public void run() {
                if (isDatabaseBrokenError(errorMessage) || isDiskFullError(errorMessage) || isDiskError(errorMessage)) {
                    processExternalError();
                    return;
                }

                errorThrowTime.set(System.currentTimeMillis());
                throw new ClientError("TDLib fatal error: " + errorMessage);
            }

            private void processExternalError() {
                errorThrowTime.set(System.currentTimeMillis());
                throw new ExternalClientError("Fatal error: " + errorMessage);
            }

            final class ClientError extends Error {
                private ClientError(String message) {
                    super(message);
                }
            }

            final class ExternalClientError extends Error {
                public ExternalClientError(String message) {
                    super(message);
                }
            }

            private boolean isDatabaseBrokenError(String message) {
                return message.contains("Wrong key or database is corrupted") ||
                        message.contains("SQL logic error or missing database") ||
                        message.contains("database disk image is malformed") ||
                        message.contains("file is encrypted or is not a database") ||
                        message.contains("unsupported file format") ||
                        message.contains("Database was corrupted and deleted during execution and can't be recreated");
            }

            private boolean isDiskFullError(String message) {
                return message.contains("PosixError : No space left on device") ||
                        message.contains("database or disk is full");
            }

            private boolean isDiskError(String message) {
                return message.contains("I/O error") || message.contains("Structure needs cleaning");
            }
        }

        final AtomicLong errorThrowTime = new AtomicLong(Long.MAX_VALUE);
        new Thread(new ThrowError(errorMessage, errorThrowTime), "TDLib fatal error thread").start();

        // wait at least 10 seconds after the error is thrown
        while (errorThrowTime.get() >= System.currentTimeMillis() - 10000) {
            try {
                Thread.sleep(1000 /* milliseconds */);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
