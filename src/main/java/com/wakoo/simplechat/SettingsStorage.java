package com.wakoo.simplechat;

import javafx.scene.control.Alert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public final class SettingsStorage {
    public static final SettingsStorage SINGLETON = new SettingsStorage();
    private SettingsStorage()  {
        nickname = "user";
        listen_port = 27600;

        settings_root = Path.of(System.getProperty("user.home") + File.separator + "simplechat");
        if (Files.isDirectory(settings_root)) {
            try (FileReader nickname_fr = new FileReader(settings_root.resolve("nickname.txt").toFile())) {
                try (BufferedReader nickname_r = new BufferedReader(nickname_fr)) {
                    nickname = nickname_r.readLine();
                }
            } catch (IOException ioexcp) {
                load_display.DisplayMessage("Невозможно прочитать имя пользователя из файла");
            }
            try (FileInputStream keypair_fi = new FileInputStream(settings_root.resolve("keypair").toFile())) {
                try (ObjectInputStream keypair_in = new ObjectInputStream(keypair_fi)) {
                    my_keypair = (KeyPair) keypair_in.readObject();
                } catch (ClassNotFoundException classnfexcp) {
                    load_display.DisplayMessage("У вас нет класса для пары ключей");
                }
            } catch (FileNotFoundException fnfexcp) {
                load_display.DisplayMessage("Не удалось найти файл с парой ключей");
            } catch (IOException ioexcp) {
                load_display.DisplayMessage("Не удалось прочитать файл с парой ключей");
            }
            try (FileReader port_fr = new FileReader(settings_root.resolve("port.txt").toFile())) {
                try (BufferedReader port_r = new BufferedReader(port_fr)) {
                    listen_port = Integer.parseInt(port_r.readLine());
                } catch (NumberFormatException nfexcp) {
                    load_display.DisplayMessage("Номер порта в файле не является числом");
                }
            } catch (FileNotFoundException fnfexcp) {
                load_display.DisplayMessage("Не удалось найти файл с номером порта");
            } catch (IOException ioexcp) {
                load_display.DisplayMessage("Не удалось прочитать файл с номером порта");
            }
        } else {
            try {
                Files.createDirectory(settings_root);
            } catch (IOException ioexcp) {
                save_display.DisplayMessage("Невозможно создать каталог для хранения настроек");
            }
            NewKeyPair();
        }
    }
    public void SaveSettings() {
        try (FileWriter nickname_wr = new FileWriter(settings_root.resolve("nickname.txt").toFile(), false)) {
            nickname_wr.write(nickname);
        } catch (IOException ioexcp) {
            save_display.DisplayMessage("Невозможно записать имя пользователя в файл");
        }
        try (FileWriter port_wr = new FileWriter(settings_root.resolve("port.txt").toFile(), false)) {
            port_wr.write(String.valueOf(listen_port));
        } catch (IOException ioexcp) {
            save_display.DisplayMessage("Невозможно записать номер порта в файл");
        }
    }

    public void NewKeyPair() {
        try {
            KeyPairGenerator pairgen = KeyPairGenerator.getInstance("RSA");
            my_keypair = pairgen.genKeyPair();
            try (FileOutputStream keypair_fw = new FileOutputStream(settings_root.resolve("keypair").toFile(), false)) {
                try (ObjectOutputStream keypair_wr = new ObjectOutputStream(keypair_fw)) {
                    keypair_wr.writeObject(my_keypair);
                }
            } catch (IOException ioexcp) {
                save_display.DisplayMessage("Невозможно записать пару ключей в файл");
            }
        } catch (NoSuchAlgorithmException noalgoexcp) {
            save_display.DisplayMessage("У вас не поддерживается алгоритм RSA");
        }
    }

    private final Path settings_root;

    private String nickname;
    private int listen_port;
    private KeyPair my_keypair;

    private MsgDisplay save_display = new ErrorDisplay("Ошибка при работе с файлом конфигурации", "Невозможно сохранить сведения в файл конфигурации");
    private MsgDisplay load_display = new ErrorDisplay("Ошибка при работе с файлом конфигурации", "Невозможно прочитать сведения из файла конфигурации");

    public String getNickname() { return nickname;}
    public void   setNickname(String nickname) { this.nickname = nickname;}

    public int getListenPort() {return listen_port;}
    public void setListenPort(int port) {listen_port = port;}
}
