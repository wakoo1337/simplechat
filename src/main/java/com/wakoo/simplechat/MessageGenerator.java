package com.wakoo.simplechat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;

public abstract class MessageGenerator implements Exportable {
    MessageGenerator(final int type) {
        header = ByteBuffer.allocate(8);
        header.order(ByteOrder.LITTLE_ENDIAN);
        header.putInt(MessageDispatcher.MessageTypes.magic);
        data = new ArrayList<ByteBuffer>();
        insertMsgType(type);
        insertString(ProfileCatalog.SINGLETON.getNickname(), false);
    }
    private void SignMessage() {
            ByteBuffer signature;
            try {
                Signature signer = Signature.getInstance("SHA256withRSA");
                try {
                    signer.initSign(ProfileCatalog.SINGLETON.getClosedKey());
                    try {
                        for (ByteBuffer element : data) signer.update(element.asReadOnlyBuffer());
                        signature = ByteBuffer.wrap(signer.sign());
                        assert (signature.capacity() == 256);
                        //signature.flip();
                        data.add(0, signature);
                    } catch (SignatureException sigexcp) {
                        disp.DisplayMessage("Невозможно вычислить цифровую подпись");
                    }
                } catch (InvalidKeyException invkeyexcp) {
                    disp.DisplayMessage("Неверный объект ключа");
                }
            } catch (NoSuchAlgorithmException noalgoexcp) {
                disp.DisplayMessage("У вас не поддерживается алгоритм SHA256 с RSA");
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
        out.add(header);
        out.addAll(data);
        return out;
    }

    protected void insertString(final String str, final boolean begin) {
        final ByteBuffer str_bb = StandardCharsets.UTF_8.encode(str);
        if (!begin) {
            insertInt(str.length(), false);
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

    protected void Finish() {
        ByteBuffer okey_bb = ByteBuffer.wrap(ProfileCatalog.SINGLETON.getOpenKey().getEncoded());
        //okey_bb.flip();
        data.add(0, okey_bb);
        SignMessage();
        int acc = 0;
        for (ByteBuffer bb : data) acc += bb.capacity();
        header.putInt(acc);
        header.flip();
    }
}
