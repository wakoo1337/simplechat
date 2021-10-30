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
                ShowFileError("Невозможно прочитать имя пользователя из файла");
            }
            try (FileInputStream keypair_fi = new FileInputStream(settings_root.resolve("keypair").toFile())) {
                try (ObjectInputStream keypair_in = new ObjectInputStream(keypair_fi)) {
                    my_keypair = (KeyPair) keypair_in.readObject();
                } catch (ClassNotFoundException classnfexcp) {
                    ShowFileError("У вас нет класса для пары ключей");
                }
            } catch (FileNotFoundException fnfexcp) {
                ShowFileError("Не удалось найти файл с парой ключей");
            } catch (IOException ioexcp) {
                ShowFileError("Не удалось прочитать файл с парой ключей");
            }
        } else {
            try {
                Files.createDirectory(settings_root);
            } catch (IOException ioexcp) {
                ShowFileError("Невозможно создать каталог для хранения настроек");
            }
            try {
                KeyPairGenerator pairgen = KeyPairGenerator.getInstance("RSA");
                my_keypair = pairgen.genKeyPair();
                try (FileOutputStream keypair_fw = new FileOutputStream(settings_root.resolve("keypair").toFile(), false)) {
                    try (ObjectOutputStream keypair_wr = new ObjectOutputStream(keypair_fw)) {
                        keypair_wr.writeObject(my_keypair);
                    }
                } catch (IOException ioexcp) {
                    ShowFileError("Невозможно записать пару ключей в файл");
                }
            } catch (NoSuchAlgorithmException noalgoexcp) {
                ShowFileError("У вас не поддерживается алгоритм RSA");
            }
        }
    }
    public void SaveSettings() {
        try (FileWriter nickname_wr = new FileWriter(settings_root.resolve("nickname.txt").toFile(), false)) {
            nickname_wr.write(nickname);
        } catch (IOException ioexcp) {
            ShowFileError("Невозможно записать имя пользователя в файл");
        }
    }

    private void ShowFileError(String msg) {
        Alert msgbox = new Alert(Alert.AlertType.ERROR);
        msgbox.setTitle("Ошибка при чтении или записи настроек");
        msgbox.setContentText(msg);
        msgbox.showAndWait();
    }

    private final Path settings_root;

    private String nickname;
    private short listen_port;
    private KeyPair my_keypair;
}
