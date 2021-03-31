package alex.config;

//import alex.interceptor.HttpHandshake;
//import alex.interceptor.HttpHandshakeInterceptor;

import alex.ServerApplication;
import alex.handlers.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketWithoutStomp implements WebSocketConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketWithoutStomp.class);

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(), "/ws").addInterceptors(auctionInterceptor());
    }

    @Bean
    public HandshakeInterceptor auctionInterceptor() {
        return new HandshakeInterceptor() {

            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                           WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

                logger.info("method beforeHandshake");

                //авторизационный токен отправителя, который зареган в приложении
                String senderToken = request.getHeaders().get("Authorization").get(0);
                int messengerId = Integer.parseInt(request.getHeaders().get("MessengerId").get(0));
                String chatId = request.getHeaders().get("ChatId").get(0);

                attributes.put("senderToken", senderToken);
                attributes.put("messengerId", messengerId);
                attributes.put("chatId", chatId);

                ServerApplication.logger.info("senderToken = " + senderToken + " messengerId = " + messengerId + " chatd = " + chatId);
                return true;
            }

            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Exception exception) {

                logger.info("method afterHandshake");
            }
        };
    }
}
