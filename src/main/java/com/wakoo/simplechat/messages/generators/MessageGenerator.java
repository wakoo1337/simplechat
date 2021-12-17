package com.wakoo.simplechat.messages.generators;

import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.displays.ErrorDisplay;
import com.wakoo.simplechat.displays.MsgDisplay;
import com.wakoo.simplechat.messages.Message;
import com.wakoo.simplechat.messages.MessageTypes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;

public abstract class MessageGenerator implements Message {
    MessageGenerator(final int type) {
        header = ByteBuffer.allocate(8);
        header.order(ByteOrder.LITTLE_ENDIAN);
        header.putInt(MessageTypes.magic);
        data = new ArrayList<ByteBuffer>();
        insertMsgType(type);
        insertString(ProfileCatalog.SINGLETON.getNickname(), false);
    }

    private void signMessage() {
        ByteBuffer signature;
        try {
            Signature signer = Signature.getInstance("SHA256withRSA");
            try {
                signer.initSign(ProfileCatalog.SINGLETON.getClosedKey());
                try {
                    for (ByteBuffer element : data) signer.update(element.asReadOnlyBuffer());
                    signature = ByteBuffer.wrap(signer.sign()); // TODO сделать ключи и подписи произвольной длины
                    data.add(0, signature);
                    insertInt(signature.limit(), true);
                } catch (SignatureException sigexcp) {
                    disp.displayMessage(sigexcp, "Невозможно вычислить цифровую подпись");
                }
            } catch (InvalidKeyException invkeyexcp) {
                disp.displayMessage(invkeyexcp, "Неверный объект ключа");
            }
        } catch (NoSuchAlgorithmException noalgoexcp) {
            disp.displayMessage(noalgoexcp, "У вас не поддерживается алгоритм SHA256 с RSA");
        }
    }

    private static final MsgDisplay disp;

    static {
        disp = new ErrorDisplay();
    }

    ByteBuffer header;
    ArrayList<ByteBuffer> data;

    public ArrayList<ByteBuffer> export() {
        ArrayList<ByteBuffer> out = new ArrayList<ByteBuffer>();
        out.add(header.asReadOnlyBuffer());
        for (ByteBuffer bb : data) out.add(bb.asReadOnlyBuffer().rewind());
        return out;
    }

    protected void insertString(final String str, final boolean begin) {
        final ByteBuffer str_bb = StandardCharsets.UTF_8.encode(str);
        if (!begin) {
            insertInt(str_bb.limit(), false);
            data.add(str_bb);
        } else {
            data.add(0, str_bb);
            insertInt(str.length(), true);
        }
    }

    protected void insertMsgType(final int type) {
        insertInt(type, false);
    }

    protected void insertInt(final int value, final boolean begin) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(value);
        bb.flip();
        if (begin) data.add(0, bb);
        else data.add(bb);
    }

    protected void finish() {
        ByteBuffer okey_bb = ByteBuffer.wrap(ProfileCatalog.SINGLETON.getOpenKey().getEncoded());
        data.add(0, okey_bb);
        insertInt(okey_bb.limit(), true);
        signMessage();
        int acc = 0;
        for (ByteBuffer bb : data) acc += bb.limit();
        header.putInt(acc);
        header.flip();
    }

    public String getNickname() {
        return ProfileCatalog.SINGLETON.getNickname();
    }

    public String getSignOk() {
        return " [\uD83D\uDD12] ";
    }
}
