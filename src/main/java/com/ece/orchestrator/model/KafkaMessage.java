package com.ece.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaMessage {

   private String transactionId;
   private int gameCode;
   private int messageCode;
   private int terminalId;
   private String region;
    private int regionCode;
   private String resellerCode;
   private List<Sale> sales;

   @Data
   public static class Sale{
       private long ts;
       private int transactionId;
       private SaleDetails sale;

   }

   @Data
   public static class SaleDetails{
       private Event event;
       private List<PlayedNumbers> playedNumbers;
   }

   @Data
   public static class Event{
       private int num;
       private int year;
   }

   @Data
   public static class PlayedNumbers{
       private int id;
       private List<Integer> numbers;
       private List<Integer> specialNumbers;
   }




}
