package alex.handlers;

import alex.ServerApplication;
import alex.entity.DialogToUser;
import alex.model.Dialog;
import alex.service.DialogToUserService;
import it.tdlight.common.ExceptionHandler;
import it.tdlight.common.ResultHandler;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi;
import it.tdlight.tdlight.ClientManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TelegClientNew class for TDLib usage from Java.
 * (Based on the official TDLib java TelegClientNew)
 */
public final class TelegClient {
    private TelegramClient client = null;
    private String phoneNumber = "";
    private String code = "";

    private int accountsNumber = 0;

    private TdApi.AuthorizationState authorizationState = null;
    private volatile boolean haveAuthorization = false;
    private volatile boolean needQuit = false;
    private volatile boolean canQuit = false;

    private final ResultHandler defaultHandler = new DefaultHandler();

    private final Lock getMeLock = new ReentrantLock();
    private final Condition gotMeInfo = getMeLock.newCondition();

    private final Lock authorizationLock = new ReentrantLock();
    private final Condition gotAuthorization = authorizationLock.newCondition();

    private final Lock lastMsgLock = new ReentrantLock();
    private final Condition lastMsgCondition = lastMsgLock.newCondition();

    private final Lock authCodeLock = new ReentrantLock();
    private final Condition authCodeCondition = authCodeLock.newCondition();

    private final Lock messageLock = new ReentrantLock();
    private final Condition messageCondition = messageLock.newCondition();

    private final Lock gciLock = new ReentrantLock();
    private final Condition gciCondition = gciLock.newCondition();

    private final Lock chatPhotoLock = new ReentrantLock();
    private final Condition chatPhotoCondition = chatPhotoLock.newCondition();

    private final Lock chatHistoryLock = new ReentrantLock();
    private final Condition chatHistoryCondition = chatHistoryLock.newCondition();

    private final ConcurrentMap<Integer, TdApi.User> users = new ConcurrentHashMap<Integer, TdApi.User>();
    private final ConcurrentMap<Integer, TdApi.BasicGroup> basicGroups = new ConcurrentHashMap<Integer, TdApi.BasicGroup>();
    private final ConcurrentMap<Integer, TdApi.Supergroup> supergroups = new ConcurrentHashMap<Integer, TdApi.Supergroup>();
    private final ConcurrentMap<Integer, TdApi.SecretChat> secretChats = new ConcurrentHashMap<Integer, TdApi.SecretChat>();

    private final ConcurrentMap<Long, TdApi.Chat> chats = new ConcurrentHashMap<Long, TdApi.Chat>();
    private final NavigableSet<OrderedChat> mainChatList = new TreeSet<OrderedChat>();
    private boolean haveFullMainChatList = false;

    private final ConcurrentMap<Integer, TdApi.UserFullInfo> usersFullInfo = new ConcurrentHashMap<Integer, TdApi.UserFullInfo>();
    private final ConcurrentMap<Integer, TdApi.BasicGroupFullInfo> basicGroupsFullInfo = new ConcurrentHashMap<Integer, TdApi.BasicGroupFullInfo>();
    private final ConcurrentMap<Integer, TdApi.SupergroupFullInfo> supergroupsFullInfo = new ConcurrentHashMap<Integer, TdApi.SupergroupFullInfo>();

    private final String newLine = System.getProperty("line.separator");
    private final String commandsLine = "Enter command (gcs - GetChats, gc <chatId> - GetChat, me - GetMe, sm <chatId> <message> - SendMessage, lo - LogOut, q - Quit): ";
    private volatile String currentPrompt = null;

    private boolean isBeen = false;
//    private int directoryNumber = 0;

    private int userId = 0;
    private boolean flag = true;
    private volatile List<Dialog> dialogs = new ArrayList<>();
    private volatile int numberOfFiles = 0;

    private String prefix = "http://dry-brook-08386.herokuapp.com";

    @Autowired
    private DialogToUserService dialogToUserService;



    public int getUserId(){
        return this.userId;
    }

    public TelegClient() {
    }

//    public int getClientId() {
//        if (userId != 0) {
//            return userId;
//        }
//
//        final boolean[] needLocked = {true};
//
//        client.send(new TdApi.GetMe(), new ResultHandler() {
//            @Override
//            public void onResult(TdApi.Object object) {
//                switch (object.getConstructor()) {
//                    case TdApi.Error.CONSTRUCTOR:
//                        System.err.println("Receive an error for GetMe:" + newLine + object);
//                        break;
//                    case TdApi.User.CONSTRUCTOR:
//                        userId = ((TdApi.User) object).id;
//                        getMeLock.lock();
//                        try {
//                            needLocked[0] = false;
//                            gotMeInfo.signal();
//                        } finally {
//                            getMeLock.unlock();
//                        }
//                        break;
//
//                    default:
//                        System.err.println("Receive wrong response from TDLib:" + newLine + object);
//                }
//            }
//        });
//
//        getMeLock.lock();
//        try {
//            if (needLocked[0]) {
//                gotMeInfo.awaitUninterruptibly();
//            }
//        } finally {
//            getMeLock.unlock();
//        }
//        return userId;
//    }

    public String getMessageType(TdApi.Message message) {
        int messageUserId = 0;
//        int userId = getClientId();
        boolean isChat = false;
        ServerApplication.logger.info(userId + " " + messageUserId);

        switch (message.sender.getConstructor()) {
            case TdApi.MessageSenderChat.CONSTRUCTOR:
                isChat = true;
                break;

            case TdApi.MessageSenderUser.CONSTRUCTOR:
                messageUserId = ((TdApi.MessageSenderUser) message.sender).userId;
                break;
            default:
        }
        String type = "in";
        if (!isChat && messageUserId == userId) {
            type = "out";
        }

        return type;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCode(String code) {
        this.code = code;
        ServerApplication.logger.info("code = " + code);
        authCodeLock.lock();
        try {
            ServerApplication.logger.info("after authCodeLock.lock();");
            authCodeCondition.signal();
        } catch (Exception e) {
            ServerApplication.logger.error(e.getMessage());
        } finally {
            authCodeLock.unlock();
            ServerApplication.logger.info("after authCodeLock.unlock();");
        }

    }

    public TelegramClient createNewClient(int directoryNumber) {
        this.accountsNumber = directoryNumber;
        client = ClientManager.create(new UpdateHandler(), new ErrorHandler(), new ErrorHandler());
//        client.initialize(new UpdateHandler(), new ErrorHandler(), new ErrorHandler());
        client.execute(new TdApi.SetLogVerbosityLevel(0));
        if (client.execute(new TdApi.SetLogStream(new TdApi.LogStreamFile("tdlib.log", 1 << 27, false))) instanceof TdApi.Error) {
            throw new IOError(new IOException("Write access to the current directory is required"));
        }
        return client;
    }

    //Стоит сразу в контроллере принимать fromMessageId, тогда можно обойтись без рекурсии
    public TdApi.Message[] getHistoryFromChat(long chatId, long fromMessageId, int limit) {
        final TdApi.Messages[] messages = {null};
//        ServerApplication.logger.info("chat: " + chats.get(chatId).lastMessage);
//        long id =  ;
        final boolean[] needLocked = {true};
        client.send(new TdApi.GetChatHistory(chatId, fromMessageId, 0, limit, false), new ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                switch (object.getConstructor()) {
                    case TdApi.Error.CONSTRUCTOR:
                        System.err.println("Receive an error for GetChatHistory:" + newLine + object);
                        break;
                    case TdApi.Messages.CONSTRUCTOR:
                        messages[0] = (TdApi.Messages) object;
                        ServerApplication.logger.info(messages[0].totalCount + " = totalCount");
                        chatHistoryLock.lock();
                        try {
                            needLocked[0] = false;
                            chatHistoryCondition.signal();
                        } finally {
                            chatHistoryLock.unlock();
                        }
                        break;

                    default:
                        System.err.println("Receive wrong response from TDLib:" + newLine + object);
                }
            }
        });

        chatHistoryLock.lock();
        try {
            if (needLocked[0]) {
                chatHistoryCondition.awaitUninterruptibly();
            }
        } finally {
            chatHistoryLock.unlock();
        }

        ServerApplication.logger.info(messages[0].totalCount + " = count");
        if (messages[0].totalCount == 1 && limit > 1) {
            ServerApplication.logger.info("return statement id = " + messages[0].messages[0].id);
            return getHistoryFromChat(chatId, messages[0].messages[0].id, limit);
        }

        return messages[0].messages;
    }

    //если происходит повторная отправка запроса, а предыдущий еще не успел выполниться
    //можно поставить на метод synchronized и не забыать при каждом вызове метода делать numberOfFiles = 0
    public synchronized List<Dialog> gci() {
        numberOfFiles = 0;
//        if(dialogs.size()>0){
//            return dialogs;
//        }

        dialogs.clear();

        int limit = 20;
        //попробовать возвращать число, чтобы избавиться от лишних await
        getMainChatList(limit);


        ServerApplication.logger.info("before gciLock.lock() await");
        gciLock.lock();
        try {
            //синхронизировать ??
            if (isBeen) {
                gciCondition.awaitUninterruptibly();
            }
        } catch (Exception e) {
            ServerApplication.logger.error(e.getMessage());
        } finally {
            gciLock.unlock();
        }
        ServerApplication.logger.info("after gciLock.unlock()");
        final boolean[] needLocked = {true};
        // have enough chats in the chat list to answer request
        synchronized (mainChatList) {
            Iterator<OrderedChat> iter = mainChatList.iterator();
            ServerApplication.logger.info("формируется список");
            ServerApplication.logger.info("First " + limit + " chat(s) out of " + mainChatList.size() + " known chat(s):");
            for (int i = 0; i < mainChatList.size() && iter.hasNext(); i++) {
                long chatId = iter.next().chatId;
                TdApi.Chat chat = chats.get(chatId);
                synchronized (chat) {
                    Dialog d = new Dialog(chatId, chat.title);
                    needLocked[0] = true;

                    //вставка в таблицу значений
//                    dialogToUserService.saveDialogToUser(new DialogToUser());



                    client.send(new TdApi.GetChat(chatId), new ResultHandler() {
                        @Override
                        public void onResult(TdApi.Object object) {
                            switch (object.getConstructor()) {
                                case TdApi.Error.CONSTRUCTOR:
                                    System.err.println("Receive an error for gci:" + newLine + object);
                                    break;
                                case TdApi.Chat.CONSTRUCTOR:
                                    TdApi.Chat chat1 = (TdApi.Chat) object;
//                                    ServerApplication.logger.info("chat1 = " + chat1);

                                    try {
//                                        d.setLastMsg_date(new Date(chat1.lastMessage.date * 1000L));
                                        d.setLastMsg_date(chat1.lastMessage.date);
                                    } catch (Exception e) {
                                        d.setLastMsg_date(-1);
                                    }

                                    try {
                                        d.setLastMsg_text(((TdApi.MessageText) chat1.lastMessage.content).text.text);
                                    } catch (Exception e) {
                                        d.setLastMsg_text("не удалось конвертировать");
                                    }

//                                    getClientId();
                                    if (chat1.lastMessage != null) {
                                        d.setType(getMessageType(chat1.lastMessage));
                                    } else {
                                        d.setType("");
                                    }

                                    lastMsgLock.lock();
                                    try {
                                        needLocked[0] = false;
                                        lastMsgCondition.signal();
                                    } finally {
                                        ServerApplication.logger.info("after signal 1");
                                        lastMsgLock.unlock();
                                    }
                                    ServerApplication.logger.info("step 1");
                                    break;
                                default:
                                    System.err.println("Receive wrong response from TDLib:" + newLine + object);
                            }
                        }
                    });
                    ServerApplication.logger.info("step 2");

                    ServerApplication.logger.info("before await 1");
                    lastMsgLock.lock();
                    try {
                        if (needLocked[0]) {
                            lastMsgCondition.awaitUninterruptibly();
                        }
                        ServerApplication.logger.info("step 3");

                    } finally {
                        lastMsgLock.unlock();
                        ServerApplication.logger.info("step 4");

                    }
                    ServerApplication.logger.info("after await 1");


                    //Добавляем title, chatId, lastMsg_date, lastMsg_text
                    dialogs.add(d);

                    needLocked[0] = true;
                    if (chat.photo != null) {
                        int id = chat.photo.small.id;
                        d.setPhoto_id(id);
                        ServerApplication.logger.info(id + " = id " + chat.title + " = title");

//                        int finalI = i;
                        client.send(new TdApi.DownloadFile(id, 1, 0, 0, false), new ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                switch (object.getConstructor()) {
                                    case TdApi.Error.CONSTRUCTOR:
                                        System.err.println("Receive an error for gci:" + newLine + object);
                                        break;
                                    case TdApi.File.CONSTRUCTOR:

                                        //Важно, так как файл дважды скачан не будет = > при обновлении
                                        //нужно следить, есть ли path или нет => СКОРЕЕ ВСЕГО ОН ОБНОВИТ НУЖНЫЕ ИКОНКИ
                                        //=> старые нужно удалять
                                        String path = ((TdApi.File) object).local.path;
                                        if (path.length() == 0) {
                                            ServerApplication.logger.info("numberOfFiles = " + numberOfFiles + " path = " + path);
                                            numberOfFiles++;
                                        } else {
                                            d.setIcon(path);
                                        }

                                        chatPhotoLock.lock();
                                        ServerApplication.logger.info("before signal 2");
                                        try {
                                            needLocked[0] = false;
                                            chatPhotoCondition.signal();
                                        } finally {
                                            ServerApplication.logger.info("after signal 2");
                                            chatPhotoLock.unlock();
                                        }
                                        break;
                                    default:
                                        System.err.println("Receive wrong response from TDLib:" + newLine + object);
                                }
                            }
                        });

                        chatPhotoLock.lock();
                        try {
                            ServerApplication.logger.info("before await 2");
                            if (needLocked[0]) {
                                chatPhotoCondition.awaitUninterruptibly();
                            }
                        } finally {
                            ServerApplication.logger.info("after await 2");
                            chatPhotoLock.unlock();
                        }
                        ServerApplication.logger.info("SPACE");
                    }
                }
            }
        }

        isBeen = false;
        ServerApplication.logger.info(dialogs.size() + " elements");

        ServerApplication.logger.info("before while numberOfFiles = " + numberOfFiles);
        while (numberOfFiles > 0) {

        }
        ServerApplication.logger.info("after while number = " + numberOfFiles);
        //не возвращать до тех пор, пока UpdateFile не заполнит dialogs путями
        return dialogs;
    }

    public TdApi.Message sendMessage(String chatId, String content) {
        return sendMessage(getChatId(chatId), content);
    }

    public void logOut() {
        haveAuthorization = false;

        //Удаление происходит при Update
        client.send(new TdApi.LogOut(), defaultHandler);

//        try {
//            deleteFile();
//        } catch (IOException e) {
//            ServerApplication.logger.info("ошибка удаления");
//        }
    }


    private boolean deleteDirectory(String dirName) {
        String userDirectory = new File("").getAbsolutePath();
        ServerApplication.logger.info(userDirectory);
        File realFile = new File(userDirectory + "/" + dirName);
        ServerApplication.logger.info(realFile.getAbsolutePath());

        return deleteAllDirs(realFile);
    }

    private boolean deleteAllDirs(File directoryToBeDeleted) {

        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteAllDirs(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    private void print(String str) {
        if (currentPrompt != null) {
            ServerApplication.logger.info("");
        }
        ServerApplication.logger.info(str);
        if (currentPrompt != null) {
            System.out.print(currentPrompt);
        }
    }

    public TdApi.Message[] getHistoryFromChat(long chatId) {
        final TdApi.Messages[] messages = {null};
        final boolean[] needLocked = {true};
        client.send(new TdApi.GetChatHistory(chatId, 0, 0, 12, false), new ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                switch (object.getConstructor()) {
                    case TdApi.Error.CONSTRUCTOR:
                        System.err.println("Receive an error for GetChats:" + newLine + object);
                        break;
                    case TdApi.Messages.CONSTRUCTOR:
                        messages[0] = (TdApi.Messages) object;

                        chatHistoryLock.lock();
                        try {
                            needLocked[0] = false;
                            chatHistoryCondition.signal();
                        } finally {
                            chatHistoryLock.unlock();
                        }
                        break;

                    default:
                        System.err.println("Receive wrong response from TDLib:" + newLine + object);
                }
            }
        });

        chatHistoryLock.lock();
        try {
            if (needLocked[0]) {
                chatHistoryCondition.awaitUninterruptibly();
            }
        } finally {
            chatHistoryLock.unlock();
        }

        ServerApplication.logger.info(messages[0].totalCount + " count");
        return messages[0].messages;
    }

    private void setChatPositions(TdApi.Chat chat, TdApi.ChatPosition[] positions) {
        synchronized (mainChatList) {
            synchronized (chat) {
                for (TdApi.ChatPosition position : chat.positions) {
                    if (position.list.getConstructor() == TdApi.ChatListMain.CONSTRUCTOR) {
                        boolean isRemoved = mainChatList.remove(new OrderedChat(chat.id, position));
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

    private void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
        if (authorizationState != null) {
            this.authorizationState = authorizationState;
        }
        switch (this.authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                ServerApplication.logger.info("onAuthorizationStateUpdated AuthorizationStateWaitTdlibParameters");
                TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
                parameters.databaseDirectory = "tdlib" + accountsNumber;
                parameters.useMessageDatabase = true;
                parameters.useSecretChats = true;
//                parameters.apiId = 94575;
//                parameters.apiHash = "a3406de8d171bb422bb6ddf3bbd800e2";
                parameters.apiId = 1964266;
                parameters.apiHash = "8cc80b22c527f56e31a42c95721f4c9a";
                parameters.systemLanguageCode = "en";
                parameters.deviceModel = "Desktop";
                parameters.applicationVersion = "1.0";
                parameters.enableStorageOptimizer = true;

                client.send(new TdApi.SetTdlibParameters(parameters), new AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                ServerApplication.logger.info("onAuthorizationStateUpdated AuthorizationStateWaitEncryptionKey");
                client.send(new TdApi.CheckDatabaseEncryptionKey(), new AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
                ServerApplication.logger.info("onAuthorizationStateUpdated AuthorizationStateWaitPhoneNumber");
//                String phoneNumber = promptString("Please enter phone number: ");
                client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null), new AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR: {
                ServerApplication.logger.info("onAuthorizationStateUpdated AuthorizationStateWaitOtherDeviceConfirmation");
                String link = ((TdApi.AuthorizationStateWaitOtherDeviceConfirmation) this.authorizationState).link;
                ServerApplication.logger.info("Please confirm this login link on another device: " + link);
                break;
            }
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR: {
                ServerApplication.logger.info("onAuthorizationStateUpdated AuthorizationStateWaitCode");
//                String code = promptString("Please enter authentication code: ");
                ServerApplication.logger.info("authCodeLock.lock()");
                authCodeLock.lock();
                try {
                    ServerApplication.logger.info("before awaitNanos");
                    authCodeCondition.awaitNanos(120_000_000_000l);
                    ServerApplication.logger.info("after awaitNanos");
//                    if(code==null || code.length()==0) {
                    //авторизация провалилась=>нужно удалить образовавшуюся папку


//                    }else{
                    client.send(new TdApi.CheckAuthenticationCode(code), new AuthorizationRequestHandler());
//                    }
                } catch (Exception e) {
                    ServerApplication.logger.error("1111" + e.getMessage());

                } finally {
                    ServerApplication.logger.info("authCodeLock.unlock()");
                    authCodeLock.unlock();
                }
                break;
            }
            case TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR: {
                ServerApplication.logger.info("onAuthorizationStateUpdated AuthorizationStateWaitRegistration");
                String firstName = promptString("Please enter your first name: ");
                String lastName = promptString("Please enter your last name: ");
                client.send(new TdApi.RegisterUser(firstName, lastName), new AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR: {
                ServerApplication.logger.info("onAuthorizationStateUpdated AuthorizationStateWaitPassword");
                String password = promptString("Please enter password: ");
                client.send(new TdApi.CheckAuthenticationPassword(password), new AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                ServerApplication.logger.info("onAuthorizationStateUpdated AuthorizationStateReady");
                haveAuthorization = true;
                authorizationLock.lock();
                try {
                    gotAuthorization.signal();
                } finally {
                    authorizationLock.unlock();
                }
                break;
            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                ServerApplication.logger.info("onAuthorizationStateUpdated AuthorizationStateLoggingOut");
                haveAuthorization = false;
                print("Logging out");

                if (deleteDirectory("tdlib" + accountsNumber)) {
                    ServerApplication.logger.info("file is deleted");
                } else {
                    ServerApplication.logger.info("file is not deleted");
                }
                break;
            case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
                ServerApplication.logger.info("onAuthorizationStateUpdated AuthorizationStateClosing");
                haveAuthorization = false;
                print("Closing");


                break;
            case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
                ServerApplication.logger.info("onAuthorizationStateUpdated AuthorizationStateClosed");
                print("Closed");
//                if (!needQuit) {
//                    client = ClientManager.create(new UpdateHandler(), new ErrorHandler(), new ErrorHandler()); // recreate client after previous has closed
////                    client.initialize(new UpdateHandler(), new ErrorHandler(), new ErrorHandler());
//                } else {
//                    canQuit = true;
//                }
                break;
            default:
                System.err.println("Unsupported authorization state:" + newLine + this.authorizationState);
        }
    }

    private int toInt(String arg) {
        int result = 0;
        try {
            result = Integer.parseInt(arg);
        } catch (NumberFormatException ignored) {
        }
        return result;
    }

    private long getChatId(String arg) {
        long chatId = 0;
        try {
            chatId = Long.parseLong(arg);
        } catch (NumberFormatException ignored) {
        }
        return chatId;
    }

    private String promptString(String prompt) {
        System.out.print(prompt);
        currentPrompt = prompt;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String str = "";
        try {
            str = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentPrompt = null;
        return str;
    }

    private void /*Map<Long, String>*/ getMainChatList(final int limit) {
        ServerApplication.logger.info("before synchronized");
        synchronized (mainChatList) {
            ServerApplication.logger.info("before first if, where haveFullMainChatList = " + haveFullMainChatList + " and mainChatList.size() = " + mainChatList.size());
            if (!haveFullMainChatList && limit > mainChatList.size()) {

                isBeen = true;
                // have enough chats in the chat list or chat list is too small
                long offsetOrder = Long.MAX_VALUE;
                long offsetChatId = 0;

                ServerApplication.logger.info("before second if mainChatList.isEmpty() = " + mainChatList.isEmpty());
                if (!mainChatList.isEmpty()) {
                    OrderedChat last = mainChatList.last();
                    offsetOrder = last.position.order;
                    offsetChatId = last.chatId;
                }
                ServerApplication.logger.info("before send");
                client.send(new TdApi.GetChats(new TdApi.ChatListMain(), offsetOrder, offsetChatId, limit - mainChatList.size()), new ResultHandler() {
                    @Override
                    public void onResult(TdApi.Object object) {
                        switch (object.getConstructor()) {
                            case TdApi.Error.CONSTRUCTOR:
                                ServerApplication.logger.info("error");
                                System.err.println("Receive an error for GetChats:" + newLine + object);
                                break;
                            case TdApi.Chats.CONSTRUCTOR:
                                ServerApplication.logger.info("not error");
                                long[] chatIds = ((TdApi.Chats) object).chatIds;
                                ServerApplication.logger.info("TdApi.Chats.CONSTRUCTOR");
                                if (chatIds.length == 0) {
                                    ServerApplication.logger.info("lenght = 0");
                                    synchronized (mainChatList) {
                                        haveFullMainChatList = true;
                                    }
                                }
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

            ServerApplication.logger.info("before gciLock.lock() signal");
            gciLock.lock();
            try {
                gciCondition.signal();
            } finally {
                gciLock.unlock();
            }
            ServerApplication.logger.info("after gciLock.lock()");

//            ServerApplication.logger.info("after send");
//            // have enough chats in the chat list to answer request
//            java.util.Iterator<OrderedChat> iter = mainChatList.iterator();
//            Map<Long, String> map = new HashMap<>();
////            ServerApplication.logger.info();
//            ServerApplication.logger.info("First " + limit + " chat(s) out of " + mainChatList.size() + " known chat(s):");
//            for (int i = 0; i < limit && iter.hasNext(); i++) {
//                long chatId = iter.next().chatId;
//                TdApi.Chat chat = chats.get(chatId);
//                synchronized (chat) {
//                    map.put(chatId, chat.title);
////                    ServerApplication.logger.info(chatId + ": " + chat.title);
//                }
//            }
//            return map;
//            print("");
        }
    }

    private TdApi.Message sendMessage(long chatId, String message) {
        // initialize reply markup just for testing
        TdApi.InlineKeyboardButton[] row = {new TdApi.InlineKeyboardButton("https://telegram.org?1", new TdApi.InlineKeyboardButtonTypeUrl()), new TdApi.InlineKeyboardButton("https://telegram.org?2", new TdApi.InlineKeyboardButtonTypeUrl()), new TdApi.InlineKeyboardButton("https://telegram.org?3", new TdApi.InlineKeyboardButtonTypeUrl())};
        TdApi.ReplyMarkup replyMarkup = new TdApi.ReplyMarkupInlineKeyboard(new TdApi.InlineKeyboardButton[][]{row, row, row});

        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(message, null), false, true);
//        client.send(new TdApi.SendMessage(chatId, 0, 0, null, replyMarkup, content), defaultHandler);
//        final TdApi.Message[] apiMessage = {null};
        final TdApi.Message[] apiMessage = new TdApi.Message[1];

        client.send(new TdApi.SendMessage(chatId, 0, 0, null, replyMarkup, content), new ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                print(object.toString());
                apiMessage[0] = (TdApi.Message) object;
                ServerApplication.logger.info(apiMessage[0].toString());

                messageLock.lock();
                try {
                    messageCondition.signal();
                } finally {
                    messageLock.unlock();
                }
//                apiMessage[0] = (TdApi.Message)object;
            }
        });

        messageLock.lock();
        try {
            messageCondition.awaitUninterruptibly();
        } finally {
            messageLock.unlock();
        }

        return apiMessage[0];
    }

    public class OrderedChat implements Comparable<OrderedChat> {
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

    private class DefaultHandler implements ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            print(object.toString());
        }
    }

    private class UpdateHandler implements ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
//            ServerApplication.logger.info("onResult method");
            switch (object.getConstructor()) {
                case TdApi.UpdateFile.CONSTRUCTOR:
                    ServerApplication.logger.info("UpdateHandler UpdateFile");

                    //мб нужен try
                    try {
                        TdApi.File file = ((TdApi.UpdateFile) object).file;
                        ServerApplication.logger.info("ININININININ");
                        if (file.local.path.length() != 0) {
                            ServerApplication.logger.info("path = " + file.local.path);

                            for (Dialog d :
                                    dialogs) {
                                if (d.getPhoto_id() == file.id) {
                                    ServerApplication.logger.info("set path for id = " + file.id);
                                    d.setIcon(prefix + file.local.path);
                                    break;
                                }
                            }
                            numberOfFiles--;
                        }
                    } catch (ClassCastException e) {

                    }
                    break;

                case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                    ServerApplication.logger.info("Update Handler UpdateAuthorizationState");
                    onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
                    break;

                case TdApi.UpdateUser.CONSTRUCTOR:
                    TdApi.UpdateUser updateUser = (TdApi.UpdateUser) object;
                    ServerApplication.logger.info("Update Handler UpdateUser");
                    users.put(updateUser.user.id, updateUser.user);
                    if(flag){
                        flag=false;
                        userId = updateUser.user.id;
                    }
                    break;
                case TdApi.UpdateUserStatus.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateUserStatus");
                    TdApi.UpdateUserStatus updateUserStatus = (TdApi.UpdateUserStatus) object;
                    TdApi.User user = users.get(updateUserStatus.userId);
                    synchronized (user) {
                        user.status = updateUserStatus.status;
                    }
                    break;
                }
                case TdApi.UpdateBasicGroup.CONSTRUCTOR:
//                    ServerApplication.logger.info("Update Handler UpdateBasicGroup");
                    TdApi.UpdateBasicGroup updateBasicGroup = (TdApi.UpdateBasicGroup) object;
                    basicGroups.put(updateBasicGroup.basicGroup.id, updateBasicGroup.basicGroup);
                    break;
                case TdApi.UpdateSupergroup.CONSTRUCTOR:
//                    ServerApplication.logger.info("Update Handler UpdateSupergroup");
                    TdApi.UpdateSupergroup updateSupergroup = (TdApi.UpdateSupergroup) object;
                    supergroups.put(updateSupergroup.supergroup.id, updateSupergroup.supergroup);
                    break;
                case TdApi.UpdateSecretChat.CONSTRUCTOR:
//                    ServerApplication.logger.info("Update Handler UpdateSecretChat");
                    TdApi.UpdateSecretChat updateSecretChat = (TdApi.UpdateSecretChat) object;
                    secretChats.put(updateSecretChat.secretChat.id, updateSecretChat.secretChat);
                    break;

                case TdApi.UpdateNewChat.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateNewChat");
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
//                    ServerApplication.logger.info("Update Handler UpdateChatTitle");
                    TdApi.UpdateChatTitle updateChat = (TdApi.UpdateChatTitle) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.title = updateChat.title;
                    }
                    break;
                }
                case TdApi.UpdateChatPhoto.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatPhoto");
                    TdApi.UpdateChatPhoto updateChat = (TdApi.UpdateChatPhoto) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.photo = updateChat.photo;
                    }
                    break;
                }
                case TdApi.UpdateChatLastMessage.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatLastMessage");
                    TdApi.UpdateChatLastMessage updateChat = (TdApi.UpdateChatLastMessage) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.lastMessage = updateChat.lastMessage;
                        setChatPositions(chat, updateChat.positions);
                    }
                    break;
                }
                case TdApi.UpdateChatPosition.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatPosition");
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
//                    ServerApplication.logger.info("Update Handler UpdateChatReadInbox");
                    TdApi.UpdateChatReadInbox updateChat = (TdApi.UpdateChatReadInbox) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.lastReadInboxMessageId = updateChat.lastReadInboxMessageId;
                        chat.unreadCount = updateChat.unreadCount;
                    }
                    break;
                }
                case TdApi.UpdateChatReadOutbox.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatReadOutbox");
                    TdApi.UpdateChatReadOutbox updateChat = (TdApi.UpdateChatReadOutbox) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.lastReadOutboxMessageId = updateChat.lastReadOutboxMessageId;
                    }
                    break;
                }
                case TdApi.UpdateChatUnreadMentionCount.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatUnreadMentionCount");
                    TdApi.UpdateChatUnreadMentionCount updateChat = (TdApi.UpdateChatUnreadMentionCount) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.unreadMentionCount = updateChat.unreadMentionCount;
                    }
                    break;
                }
                case TdApi.UpdateMessageMentionRead.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateMessageMentionRead");
                    TdApi.UpdateMessageMentionRead updateChat = (TdApi.UpdateMessageMentionRead) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.unreadMentionCount = updateChat.unreadMentionCount;
                    }
                    break;
                }
                case TdApi.UpdateChatReplyMarkup.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatReplyMarkup");
                    TdApi.UpdateChatReplyMarkup updateChat = (TdApi.UpdateChatReplyMarkup) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.replyMarkupMessageId = updateChat.replyMarkupMessageId;
                    }
                    break;
                }
                case TdApi.UpdateChatDraftMessage.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatDraftMessage");
                    TdApi.UpdateChatDraftMessage updateChat = (TdApi.UpdateChatDraftMessage) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.draftMessage = updateChat.draftMessage;
                        setChatPositions(chat, updateChat.positions);
                    }
                    break;
                }
                case TdApi.UpdateChatPermissions.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatPermissions");
                    TdApi.UpdateChatPermissions update = (TdApi.UpdateChatPermissions) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.permissions = update.permissions;
                    }
                    break;
                }
                case TdApi.UpdateChatNotificationSettings.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatNotificationSettings");
                    TdApi.UpdateChatNotificationSettings update = (TdApi.UpdateChatNotificationSettings) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.notificationSettings = update.notificationSettings;
                    }
                    break;
                }
                case TdApi.UpdateChatDefaultDisableNotification.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatDefaultDisableNotification");
                    TdApi.UpdateChatDefaultDisableNotification update = (TdApi.UpdateChatDefaultDisableNotification) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.defaultDisableNotification = update.defaultDisableNotification;
                    }
                    break;
                }

                case TdApi.NotificationTypeNewMessage.CONSTRUCTOR:
                    TdApi.Message mess = (TdApi.Message)object;
                    System.out.println("New Message recieved " + mess.toString());


                case TdApi.UpdateChatIsMarkedAsUnread.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatIsMarkedAsUnread");
                    TdApi.UpdateChatIsMarkedAsUnread update = (TdApi.UpdateChatIsMarkedAsUnread) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.isMarkedAsUnread = update.isMarkedAsUnread;
                    }
                    break;
                }
                case TdApi.UpdateChatIsBlocked.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatIsBlocked");
                    TdApi.UpdateChatIsBlocked update = (TdApi.UpdateChatIsBlocked) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.isBlocked = update.isBlocked;
                    }
                    break;
                }
                case TdApi.UpdateChatHasScheduledMessages.CONSTRUCTOR: {
//                    ServerApplication.logger.info("Update Handler UpdateChatHasScheduledMessages");
                    TdApi.UpdateChatHasScheduledMessages update = (TdApi.UpdateChatHasScheduledMessages) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.hasScheduledMessages = update.hasScheduledMessages;
                    }
                    break;
                }

                case TdApi.UpdateUserFullInfo.CONSTRUCTOR:
//                    ServerApplication.logger.info("Update Handler UpdateUserFullInfo");
                    TdApi.UpdateUserFullInfo updateUserFullInfo = (TdApi.UpdateUserFullInfo) object;
                    usersFullInfo.put(updateUserFullInfo.userId, updateUserFullInfo.userFullInfo);
                    break;
                case TdApi.UpdateBasicGroupFullInfo.CONSTRUCTOR:
//                    ServerApplication.logger.info("Update Handler UpdateBasicGroupFullInfo");
                    TdApi.UpdateBasicGroupFullInfo updateBasicGroupFullInfo = (TdApi.UpdateBasicGroupFullInfo) object;
                    basicGroupsFullInfo.put(updateBasicGroupFullInfo.basicGroupId, updateBasicGroupFullInfo.basicGroupFullInfo);
                    break;
                case TdApi.UpdateSupergroupFullInfo.CONSTRUCTOR:
//                    ServerApplication.logger.info("Update Handler UpdateSupergroupFullInfo");
                    TdApi.UpdateSupergroupFullInfo updateSupergroupFullInfo = (TdApi.UpdateSupergroupFullInfo) object;
                    supergroupsFullInfo.put(updateSupergroupFullInfo.supergroupId, updateSupergroupFullInfo.supergroupFullInfo);
                    break;
                default:
                    // print("Unsupported update:" + newLine + object);
            }
        }
    }

    private class ErrorHandler implements ExceptionHandler {

        @Override
        public void onException(Throwable e) {
            e.printStackTrace();
        }
    }

    private class AuthorizationRequestHandler implements ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.Error.CONSTRUCTOR:
                    System.err.println("Receive an error:" + newLine + object);
//                    onAuthorizationStateUpdated(null); // repeat last action
                    deleteDirectory("tdlib" + accountsNumber);
                    break;
                case TdApi.Ok.CONSTRUCTOR:
                    // result is already received through UpdateAuthorizationState, nothing to do
                    break;
                default:
                    System.err.println("Receive wrong response from TDLib:" + newLine + object);
            }
        }
    }
}
