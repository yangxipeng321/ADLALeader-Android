package hk.com.mobileye.jason.adlaleader.net.Message;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jason on 2015/1/4.
 */
public class MsgBody {
    private ArrayList<TLVClass> mTLVList = new ArrayList<TLVClass>();

    protected MsgBody() {
    }

    public void clear() {
        mTLVList.clear();
    }

    public TLVClass add(int aTLVType, Class aValueType) {
        TLVClass objTLV = new TLVClass(aTLVType, aValueType);
        mTLVList.add(objTLV);
        return objTLV;
    }

    public void remove(int aTLVType) {
        for (TLVClass tlv : mTLVList) {
            if (tlv.getType() == aTLVType) {
                mTLVList.remove(tlv);
            }
        }
    }

    public TLVClass get(int aTLVType) {
        for (TLVClass tlv : mTLVList) {
            if (tlv.getType() == aTLVType) {
                return tlv;
            }
        }
        return null;
    }

    public int getCount() {
        return mTLVList.size();
    }

    public List<TLVClass> getTLVs () {
        return mTLVList;
    }

}
