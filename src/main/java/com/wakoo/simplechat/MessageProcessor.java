package com.wakoo.simplechat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public abstract class MessageProcessor {
    protected final byte[] sign_arr;
    protected Signature sign;
    protected final byte[] okey_arr;
    protected PublicKey okey;
    protected final String nickname;
    protected final ByteBuffer remain;
    protected boolean sign_ok;

    String marker = "";

    MessageProcessor(final int type, final ByteBuffer msg) {
        remain = msg;
        sign_arr = new byte[256];
        msg.get(sign_arr);
        okey_arr = new byte[294];
        msg.get(okey_arr);
        nickname = GetString();
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(okey_arr);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            try {
                okey = kf.generatePublic(x509);
                sign = Signature.getInstance("SHA256withRSA");
                sign.initVerify(okey);
                ByteBuffer check_bb = msg.duplicate();
                check_bb.order(ByteOrder.LITTLE_ENDIAN);
                sign.update(check_bb);
                sign_ok = sign.verify(sign_arr) && ProfileCatalog.OpenKeyStorage.SINGLETON.CheckNicknameKeyMapping(nickname, okey);
            } catch (InvalidKeySpecException e) {
                disp.DisplayMessage("Не получается сгенерировать открытый ключ");
            } catch (InvalidKeyException e) {
                disp.DisplayMessage("Ключ не проходит для проверки ЭЦП");
            } catch (SignatureException e) {
                disp.DisplayMessage("Не получается проверить ЭЦП");
            }
        } catch (NoSuchAlgorithmException e) {
            disp.DisplayMessage("Нет такого алгоритма");
        }
    }

    protected static final MsgDisplay disp = new ErrorDisplay("Ошибка при разборе сообщения");

    protected String GetString() {
        final int len = remain.getInt();
        ByteBuffer bb = ByteBuffer.allocate(len);
        for (int i=0;i < len;i++) bb.put(remain.get());
        bb.flip();
        return StandardCharsets.UTF_8.decode(bb).toString();
    }

    public boolean GetSignOk() {return sign_ok;}
}
