package com.example.demo.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Collections;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${telegram.bot.username}")
    private String botName;
    @Value("${telegram.bot.token}")
    private String botToken;


    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Проверяем, есть ли сообщение и текст в нем
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();

            // Создаем клавиатуру с кнопкой "Оставить отзыв"
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            KeyboardRow keyboardRow = new KeyboardRow(); // Создаем строку для кнопок
            keyboardRow.add("Оставить отзыв"); // Добавляем кнопку в строку
            keyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow)); // Добавляем строку в клавиатуру
            keyboardMarkup.setResizeKeyboard(true); // Автоматически подгоняем размер клавиатуры

            // Если пользователь написал /start
            if (messageText.equals("/start")) {
                SendMessage welcomeMessage = new SendMessage();
                welcomeMessage.setChatId(String.valueOf(chatId));
                welcomeMessage.setText("Добро пожаловать в нашего бота, " + userName + "!");
                welcomeMessage.setReplyMarkup(keyboardMarkup); // Устанавливаем клавиатуру
                try {
                    execute(welcomeMessage); // Отправляем сообщение с клавиатурой
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (messageText.equals("Оставить отзыв")) {
                sendMessage(chatId, "Спасибо за отзыв!");
            } else {
                // Отвечаем на любое сообщение
                sendMessage(chatId, "Привет, " + userName + "!");
            }
        }
    }

    // Метод для отправки сообщения
    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message); // Отправляем сообщение
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

}


