package org.stb.initialition;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {
    @Value("${webhook.path}")
    String webhookPath;
    @Value("${bot.name}")
    String botName;
    @Value("${bot.token}")
    String token;
    @Value("${webhook.root.path}")
    String rootPath;
}
