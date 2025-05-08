package com.Ivan.Rwalent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Enables WebSocket message handling, backed by a message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple memory-based message broker to carry messages back to the client
        // on destinations prefixed with "/topic" and "/queue"
        // "/queue" is generally used for user-specific messages
        config.enableSimpleBroker("/topic", "/queue"); 
        
        // Designates the "/app" prefix for messages that are bound for @MessageMapping-annotated methods
        // e.g., Frontend sends to /app/someEndpoint
        config.setApplicationDestinationPrefixes("/app");
        
        // Configures the prefix used to identify user destinations
        // Allows sending messages to specific users via SimpMessagingTemplate
        // e.g., template.convertAndSendToUser(userId, "/queue/notifications", payload);
        config.setUserDestinationPrefix("/user"); 
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registers the "/ws" endpoint, enabling SockJS fallback options so that alternate transports
        // may be used if WebSocket is not available.
        // SockJS is used to enable fallback options for browsers that don't support WebSocket.
        // You might need CORS configuration here if your frontend is on a different origin
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:8081") // Replace with your frontend origin if needed
                .withSockJS(); // Use SockJS for broader compatibility
                
        // You could also register an endpoint without SockJS if preferred
        // registry.addEndpoint("/websocket").setAllowedOrigins("*"); 
    }
} 