package org.brizom.aidt.gptservice.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GPTConfig {


  @Bean
  public OpenAIClient openAIClient() {
    return OpenAIOkHttpClient.fromEnv();
  }

}
