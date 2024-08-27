//package kuchat.server.common.websocket;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class WebSocketHandler extends TextWebSocketHandler {
//    /**
//     * 웹소켓 연결 성공시
//     * @param session
//     */
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session){
//        try  {
//            session.sendMessage(new TextMessage("웹소켓 연결 성공"));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }
//}
