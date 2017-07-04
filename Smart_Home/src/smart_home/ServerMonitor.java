package smart_home;



import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import org.apache.juli.logging.Log;��������juli ����tomcat��Ŀ¼��binĿ¼�µ�tomcat-juli.jar �ṩ�ģ�����������ʲô����ģ�Ҳ��֪����org.apache.commons�µ���ʲô����
//import org.apache.juli.logging.LogFactory;

import util.HTMLFilter;//�������tomcat websocket examples�����WEB-INF\classes\�����util�ļ����µ�HTMLfilter.class�ṩ�ġ�

@ServerEndpoint(value = "/ServerMonitor")
public class ServerMonitor {

    private static final Log log = LogFactory.getLog(ServerMonitor.class);

    private static final String GUEST_PREFIX = "Guest";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<ServerMonitor> connections =
            new CopyOnWriteArraySet<>();

    private final String nickname;
    private Session session;

    public ServerMonitor() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }


    @OnOpen
    public void start(Session session) {
        this.session = session;
        connections.add(this);
        String message = String.format("* %s %s", nickname, "has joined.");
        broadcast(message);
    }


    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format("* %s %s",
                nickname, "has disconnected.");
        broadcast(message);
    }


    @OnMessage
    public void incoming(String message) {
        // Never trust the client
        String filteredMessage = String.format("%s: %s",
                nickname, HTMLFilter.filter(message.toString()));
        broadcast(filteredMessage);
    }




    @OnError
    public void onError(Throwable t) throws Throwable {
        log.error("Chat Error: " + t.toString(), t);
    }


    private static void broadcast(String msg) {
        for (ServerMonitor client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                log.debug("Chat Error: Failed to send message to client", e);
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s",
                        client.nickname, "has been disconnected.");
                broadcast(message);
            }
        }
    }
}
