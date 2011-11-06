package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * A writable list. This thing uses a bit of messy reflection to preserve generic arguments...
 * <p/>
 * BTW. ALL WRITABLES USED IN HERE SHOULD HAVE A DEFAULT CONSTRUCTOR. YUP.
 *
 * @param <E>
 * @author xshi
 */
public class WritableList extends ArrayList<Writable> implements Writable {
    private static final long serialVersionUID = -125579462824808520L;

    Class<? extends Writable> storedClass;

    protected WritableList(Class<? extends Writable> storedClass) {
        this.storedClass = storedClass;
    }

    @Override
    public void readFrom(InputStream in) throws IOException {
        int length = DataTypeIO.readInt(in);
        this.clear();
        for (int i = 0; i < length; i++) {
            try {
                Writable e = storedClass.newInstance();
                e.readFrom(in);
                add(e);
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        DataTypeIO.writeInt(out, size());
        for (Writable e : this) {
            e.writeTo(out);
        }
    }
}
