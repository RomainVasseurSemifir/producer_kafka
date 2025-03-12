package com.example.producer.controller;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
public class demoProducer {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    private Logger logger = LoggerFactory.getLogger(demoProducer.class);

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/") // => GET http://localhost:8090/
    public String index(){
        return "server online";
    }

    @GetMapping("/sync/{message}")
    public String publishSync(@PathVariable("message") String message){
        String result= "";
        String topic = "app-topic";
        String key = "sync";
        final ProducerRecord<String, String> myRecord =
                new ProducerRecord<>(topic,key,message);

        try {
            kafkaTemplate.send(myRecord).get(3, TimeUnit.SECONDS);
            result = "Published successfully !";
        } catch (InterruptedException e) {
            result = "Erreur";
        } catch (ExecutionException e) {
            result = "Erreur";
        } catch (TimeoutException e) {
            result = "Timeout";
        } finally {
            kafkaTemplate.destroy();
        }
        return result;
    }

    @GetMapping("/async/{message}")
    public String publishAsync(@PathVariable("message") String message){
        String topic = "app-topic";
        String key = "async";
        final ProducerRecord<String, String> myRecord =
                new ProducerRecord<>(topic,key,message);
        // code non blockant
        CompletableFuture<SendResult<String, String>> future
                = kafkaTemplate.send(myRecord);
        future.whenComplete( (result, ex) -> {
            //if sucess
            if (ex == null){
                // traiter mon resultat
                // handleResult(result);
                logger.info("send message : "+ message
                        + " in partition : "+ result.getRecordMetadata().partition()
                        + " and with offset : "+result.getRecordMetadata().offset());
            // if faillure
            } else {
                // handleFaillure(message, ex);
                logger.info(("Erreur : "+ ex.getMessage()));
            }

        });
        return "Send async successfull !";
    }

}
